package edu.gemini.aspen.giapi.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import gov.aps.jca.*;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;
import gov.aps.jca.dbr.DBR;

import java.util.HashMap;
import java.util.Map;
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
    private ServerContext serverContext =null;
    private Context clientContext =null;
    private Thread t;
    private JCALibrary jca;
    private Map<String, Channel> channels;

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
        channels=new HashMap<String,Channel>();
        server = new DefaultServerImpl();
        if (serverContext != null) {
            throw new IllegalStateException("Tried to start the GiapiCas more than once");
        }
        serverContext = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server);
        t = new Thread(this);
        t.start();
        try{
            //TODO: this is UGLY!! need a way to see that the server is up and running
            Thread.sleep(100);
        }catch(InterruptedException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
        clientContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
    }

    /**
     * Adds a new process variable.
     *
     * @param name of the PV
     * @param type of the PV
     * @param initialValue must be an array of the appropriate type
     *
     * @throws CAException is thrown if a Channel Access error occured while creating the channel.
     * @throws java.lang.IllegalArgumentException is thrown if the channel's name is null or empty.
     * @throws java.lang.IllegalStateException if the context has been destroyed.
     */
    public void addVariable(String name, DBRType type, Object initialValue)throws CAException, IllegalArgumentException, IllegalStateException{
        server.createMemoryProcessVariable(name, type, initialValue);
        Channel ch=clientContext.createChannel(name);
        try{
            clientContext.pendIO(1);
        }catch(TimeoutException ex){
            throw new CAException(ex);
        }
        channels.put(name,ch);
    }

    /**
     * Removes a process variable
     *
     * @param name of the PV to remove
     */
    public void removeVariable(String name){
        server.unregisterProcessVaribale(name);
        channels.remove(name);
    }

    /**
     *
     * @param name
     * @param value
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered
     */
    public void put(String name, String value) throws CAException, IllegalStateException, IllegalArgumentException{
        Channel ch=channels.get(name);
        if(ch==null){
            throw new IllegalArgumentException("Process Variable not registered.");
        }
        if(!ch.getFieldType().isSTRING()){
            throw new IllegalArgumentException("Trying to write a String value in a "+ ch.getFieldType().getName()+" field.");
        }
        ch.put(value);
        try{
            ch.getContext().pendIO(1);
        }catch(TimeoutException ex){
            throw new CAException(ex);
        }
    }
    /**
     *
     * @param name
     * @param value
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered
     */
    public void put(String name, float value) throws CAException, IllegalStateException, IllegalArgumentException{
        Channel ch=channels.get(name);
        if(ch==null){
            throw new IllegalArgumentException("Process Variable not registered.");
        }
        if(!ch.getFieldType().isFLOAT()){
            throw new IllegalArgumentException("Trying to write a float value in a "+ ch.getFieldType().getName()+" field.");
        }
        ch.put(value);
        try{
            ch.getContext().pendIO(1);
        }catch(TimeoutException ex){
            throw new CAException(ex);
        }
    }
    /**
     *
     * @param name
     * @param value
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered
     */
    public void put(String name, double value) throws CAException, IllegalStateException, IllegalArgumentException{
        Channel ch=channels.get(name);
        if(ch==null){
            throw new IllegalArgumentException("Process Variable not registered.");
        }
        if(!ch.getFieldType().isDOUBLE()){
            throw new IllegalArgumentException("Trying to write a double value in a "+ ch.getFieldType().getName()+" field.");
        }
        ch.put(value);
        try{
            ch.getContext().pendIO(1);
        }catch(TimeoutException ex){
            throw new CAException(ex);
        }
    }
    /**
     *
     * @param name
     * @param value
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered
     */
    public void put(String name, int value) throws CAException, IllegalStateException, IllegalArgumentException{
        Channel ch=channels.get(name);
        if(ch==null){
            throw new IllegalArgumentException("Process Variable not registered.");
        }
        if(!ch.getFieldType().isINT()){
            throw new IllegalArgumentException("Trying to write a int value in a "+ ch.getFieldType().getName()+" field.");
        }
        ch.put(value);
        try{
            ch.getContext().pendIO(1);
        }catch(TimeoutException ex){
            throw new CAException(ex);
        }
    }
    /**
     *
     * @param name
     * @return
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered
     */
    public DBR get(String name) throws CAException, IllegalStateException, IllegalArgumentException{
        Channel ch=channels.get(name);
        if(ch==null){
            throw new IllegalArgumentException("Process Variable not registered.");
        }
        DBR dbr =ch.get();
        try{
            ch.getContext().pendIO(1);
        }catch(TimeoutException ex){
            throw new CAException(ex);
        }
        return dbr;
    }
    /**
     * Destroys the jca context and waits for the thread to return.
     *
     * @throws CAException
     * @throws IllegalStateException if the context has been destroyed.
     */
    public void stop() throws CAException, IllegalStateException{
        for(Channel channel:channels.values()){
            channel.destroy();    
        }
        channels=null;
        serverContext.destroy();
        clientContext.destroy();
        try{
            t.join();
        }catch(InterruptedException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
        server=null;
        t=null;
        serverContext =null;
        clientContext =null;
    }

    /**
     * Runs the server. This is called by Thread.start()
     */
    @Override
    public void run() {
        try {
            serverContext.run(0);
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
