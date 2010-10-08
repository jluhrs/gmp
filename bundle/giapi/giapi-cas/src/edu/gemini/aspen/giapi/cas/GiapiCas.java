package edu.gemini.aspen.giapi.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.cas.Server;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR_String;
                                     import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class GiapiCas
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
    public GiapiCas() {
       jca = JCALibrary.getInstance();
    }

    /**
     *
     * @throws IllegalStateException
     * @throws CAException
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
     *
     * @param name
     * @param type
     * @param initialValue
     */
    public void addVariable(String name, DBRType type, Object initialValue){
       server.createMemoryProcessVariable(name, type, initialValue);
    }

    /**
     *
     * @param name
     */
    public void removeVariable(String name){
       server.unregisterProcessVaribale(name);
    }

    /**
     *
     * @throws CAException
     */
    public void stop() throws CAException{
        ctxt.shutdown();
        ctxt.destroy();
        ctxt.dispose();
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
     * 
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
     *
     * @param args
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
