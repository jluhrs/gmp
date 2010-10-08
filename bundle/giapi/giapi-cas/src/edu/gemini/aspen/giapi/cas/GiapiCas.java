package edu.gemini.aspen.giapi.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class GiapiCas. Implements the bulk of the giapi-cas bundle.
 *
 * It implements the Runnable interface because we need to run the JCA server in another thread.
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class GiapiCas implements Runnable{
    private static final Logger LOG = Logger.getLogger(GiapiCas.class.getName());
    private DefaultServerImpl server;
    private ServerContext ctxt=null;
    private Thread t;
    JCALibrary jca;

    /**
     * Constructor. Gets the instance of the JCALibrary singleton
     */
    public GiapiCas() {
       jca = JCALibrary.getInstance();
    }

    /**
     * Creates a server, a jca context and spawns a new thread to run the server.
     *
     * @throws IllegalStateException if trying to start an already started server
     * @throws CAException is thrown if the jca context could not be instanciated.
     */
    public void start() throws IllegalStateException, CAException{
        server = new DefaultServerImpl();
        if (ctxt != null) {
            throw new IllegalStateException("Tried to start the GiapiCas more than once");
        }
        ctxt = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server);
        t = new Thread(this);
        t.start();
        try{
            //TODO: this is UGLY!! need a way to see that the server is up and running
            Thread.sleep(100);
        }catch(InterruptedException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
    }

    /**
     * Adds a new process variable.
     *
     * @param name of the PV
     * @param type of the PV
     * @param initialValue must be an array of the appropriate type
     */
    public void addVariable(String name, DBRType type, Object initialValue){
       server.createMemoryProcessVariable(name, type, initialValue);
    }

    /**
     * Removes a process variable
     *
     * @param name of the PV to remove
     */
    public void removeVariable(String name){
       server.unregisterProcessVaribale(name);
    }

    /**
     * Destroys the jca context and waits for the thread to return.
     *
     * @throws CAException
     * @throws IllegalStateException if the context has been destroyed.
     */
    public void stop() throws CAException, IllegalStateException{
        ctxt.destroy();
        try{
            t.join();
        }catch(InterruptedException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
        server=null;
        t=null;
        ctxt=null;
    }

    /**
     * Runs the server. This is called by Thread.start()
     */
    @Override
    public void run() {
        try {
            ctxt.run(0);
        } catch (IllegalStateException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        } catch (CAException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }

    }

    /**
     * Main method for starting the server from teh command line.
     *
     * @param args number of milliseconds to run the server
     */
    public static void main(String[] args){
        GiapiCas giapicas = new GiapiCas();
        try {

            giapicas.start();
            giapicas.addVariable("nico:test1", DBR_Int.TYPE, new int[]{-1});

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        try{
            Thread.sleep(Integer.parseInt(args[0]));
            giapicas.stop();
        }catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
