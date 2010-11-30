package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;
import gov.aps.jca.*;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class GiapiCas implements IGiapiCas {
    private static final Logger LOG = Logger.getLogger(GiapiCas.class.getName());
    private DefaultServerImpl server;
    private ServerContext serverContext =null;
    private ExecutorService executor;
    private JCALibrary jca;
    private Map<String, MemoryProcessVariable> pvs;

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
        executor = Executors.newSingleThreadExecutor();
        pvs=new HashMap<String,MemoryProcessVariable>();
        server = new DefaultServerImpl();
        if (serverContext != null) {
            throw new IllegalStateException("Tried to start the GiapiCas more than once");
        }
        serverContext = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server);
        executor.execute(new Runnable() {
            public void run() {
                try {
                    serverContext.run(0);
                } catch (IllegalStateException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                } catch (CAException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }

            }
        });

        try{
            //TODO: this is UGLY!! need a way to see that the server is up and running
            Thread.sleep(2000);
        }catch(InterruptedException ex){
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
    }

    /**
     * Adds a new process variable.
     *
     * @param name of the PV
     * @param value
     * @param values
     * @param <T> Must be one of Integer, Double, Float or String
     * @throws IllegalArgumentException if the values are of an unsupported type, or the values are not all of the same type
     */
    @Override
    public <T> void addVariable(String name, T value, T... values){
        LOG.info("Adding variable "+name);
        int length = 1 + values.length;
        if (value instanceof Integer) {
            if (values instanceof Integer[]) {
                int[] newValues = new int[length];
                newValues[0] = (Integer) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (Integer) values[i - 1];
                }
                MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_Int.TYPE, newValues);
                pvs.put(name, pv);
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        } else if (value instanceof Float) {
            if (values instanceof Float[]) {
                float[] newValues = new float[length];
                newValues[0] = (Float) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (Float) values[i - 1];
                }
                MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_Float.TYPE, newValues);
                pvs.put(name, pv);
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        }else  if (value instanceof Double) {
            if (values instanceof Double[]) {
                double[] newValues = new double[length];
                newValues[0] = (Double) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (Double) values[i - 1];
                }
                MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_Double.TYPE, newValues);
                pvs.put(name, pv);
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        }else  if (value instanceof String) {
            if (values instanceof String[]) {
                String[] newValues = new String[length];
                newValues[0] = (String) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (String) values[i - 1];
                }
                MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_String.TYPE, newValues);
                pvs.put(name, pv);
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        }else{
            throw new IllegalArgumentException("Unsupported class " + value.getClass().getName());
        }
    }

    /**
     *
     * @param name
     * @param value
     * @param values
     * @param <T> Must be one of Integer, Double, Float or String
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered, or
     *          a wrong data type is passed
     */
    @Override
    public <T> void put(String name, T value, T... values) throws CAException {
        //LOG.info("Trying to update " + name + " to " + value);
        int length = 1 + values.length;
        MemoryProcessVariable pv = pvs.get(name);
        if (pv == null) {
            throw new IllegalArgumentException("PV doesn't exist");
        }
        if (pv.getDimensionSize(0) != length) {
            throw new IllegalArgumentException("Incorrect array length");
        }
        if (value instanceof Integer) {
            if (values instanceof Integer[]) {
                if (!pv.getType().isINT()) {
                    throw new IllegalArgumentException("Trying to write an" + value.getClass().getName() + " value in a " + pv.getType().getName() + " field.");
                }
                int[] newValues = new int[length];
                newValues[0] = (Integer) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (Integer) values[i - 1];
                }
                CAStatus status = pv.write(new DBR_Int(newValues), null);
                if (status != CAStatus.NORMAL) {
                    throw new RuntimeException("MemoryProcessVariable.write(..) returned with abnormal status: " + status);
                }
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        } else if (value instanceof Float) {
            if (values instanceof Float[]) {
                if (!pv.getType().isFLOAT()) {
                    throw new IllegalArgumentException("Trying to write an" + value.getClass().getName() + " value in a " + pv.getType().getName() + " field.");
                }
                float[] newValues = new float[length];
                newValues[0] = (Float) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (Float) values[i - 1];
                }
                CAStatus status = pv.write(new DBR_Float(newValues), null);
                if (status != CAStatus.NORMAL) {
                    throw new RuntimeException("MemoryProcessVariable.write(..) returned with abnormal status: " + status);
                }
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        } else if (value instanceof Double) {
            if (values instanceof Double[]) {
                if (!pv.getType().isDOUBLE()) {
                    throw new IllegalArgumentException("Trying to write an" + value.getClass().getName() + " value in a " + pv.getType().getName() + " field.");
                }
                double[] newValues = new double[length];
                newValues[0] = (Double) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (Double) values[i - 1];
                }
                CAStatus status = pv.write(new DBR_Double(newValues), null);
                if (status != CAStatus.NORMAL) {
                    throw new RuntimeException("MemoryProcessVariable.write(..) returned with abnormal status: " + status);
                }
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        } else if (value instanceof String) {
            if (values instanceof String[]) {
                if (!pv.getType().isSTRING()) {
                    throw new IllegalArgumentException("Trying to write an" + value.getClass().getName() + " value in a " + pv.getType().getName() + " field.");
                }
                String[] newValues = new String[length];
                newValues[0] = (String) value;
                for (int i = 1; i < length; i++) {
                    newValues[i] = (String) values[i - 1];
                }
                CAStatus status = pv.write(new DBR_String(newValues), null);
                if (status != CAStatus.NORMAL) {
                    throw new RuntimeException("MemoryProcessVariable.write(..) returned with abnormal status: " + status);
                }
            } else {
                throw new IllegalArgumentException("All the values must be of the same type as the first " + value.getClass().getName());
            }
        } else {
            throw new IllegalArgumentException("Unsupported class " + value.getClass().getName());
        }
        //LOG.info("Updated " + name + " to " + value);

    }

    /**
     * Removes a process variable
     *
     * @param name of the PV to remove
     */
    public void removeVariable(String name){
        LOG.info("Removing variable "+name);
        server.unregisterProcessVaribale(name);
        pvs.remove(name).destroy();
        //LOG.info("Deleted channel "+name);
    }

    /**
     *
     * @param name
     * @return
     * @throws CAException if a Channel Exception occured while performing this operation.
     * @throws IllegalStateException if the channel is in no state to perform this operation (ie destroyed, etc...)
     * @throws java.lang.IllegalArgumentException if the variable's name is not registered
     */
    @Override
    public DBR get(String name) throws CAException{
         MemoryProcessVariable pv=pvs.get(name);
        if(pv==null){
            throw new IllegalArgumentException("Process Variable not registered.");
        }
        //LOG.info("Variable "+name+" of type "+pv.getType());
        DBR dbr=null;
        if(pv.getType().isINT()){
            dbr=new DBR_TIME_Int(pv.getDimensionSize(0));
        }else if(pv.getType().isFLOAT()){
            dbr=new DBR_TIME_Float(pv.getDimensionSize(0));
        }else if(pv.getType().isDOUBLE()){
            dbr=new DBR_TIME_Double(pv.getDimensionSize(0));
        }else if(pv.getType().isSTRING()){
            dbr=new DBR_TIME_String(pv.getDimensionSize(0));
        }else{
            throw new RuntimeException("Unsupported type");
        }
        //LOG.info("count "+dbr.getCount());
        CAStatus status =pv.read(dbr,null);
        if(status!= CAStatus.NORMAL){
             throw new RuntimeException("MemoryProcessVariable.read(..) returned with abnormal status: "+status);   
        }
        return dbr;
    }
    /**
     * Destroys the jca context and waits for the thread to return.
     *
     * @throws CAException
     * @throws java.lang.IllegalStateException if the context has been destroyed.
     */
    public void stop() throws CAException{
        for(String name:pvs.keySet()){
            MemoryProcessVariable pv = pvs.get(name);
            server.unregisterProcessVaribale(name);
            pv.destroy();
        }
        pvs=null;
        serverContext.destroy();
        executor.shutdown();
        server=null;
        serverContext =null;
    }


    /**
     * Main method for starting the server from the command line.
     *
     * @param args number of milliseconds to run the server
     */
    public static void main(String[] args){
        GiapiCas giapicas = new GiapiCas();
        try {

            giapicas.start();
            giapicas.addVariable("test", -1);

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
