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
 * Class Cas. Implements the bulk of the giapi-cas bundle.
 * <p/>
 * It implements the Runnable interface because we need to run the JCA server in another thread.
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class Cas {
    private static final Logger LOG = Logger.getLogger(Cas.class.getName());
    private DefaultServerImpl server;
    private ServerContext serverContext = null;
    private ExecutorService executor;
    private final JCALibrary jca = JCALibrary.getInstance();
    private Map<String, Channel> channels;

    /**
     * Constructor.
     */
    public Cas() {
    }

    /**
     * Creates a server, a jca context and spawns a new thread to run the server.
     *
     * @throws IllegalStateException if trying to start an already started server
     * @throws CAException           is thrown if the jca context could not be instanciated.
     */
    public void start() throws CAException {
        executor = Executors.newSingleThreadExecutor();
        channels = new HashMap<String, Channel>();
        server = new DefaultServerImpl();
        if (serverContext != null) {
            throw new IllegalStateException("Tried to start the Cas more than once");
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

        try {
            //TODO: this is UGLY!! need a way to see that the server is up and running
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static final class Channel {
        private MemoryProcessVariable pv;

        private Channel(MemoryProcessVariable pv) {
            this.pv = pv;
        }

        public void setValue(Integer value) throws CAException {
            setValue(new Integer[]{value});
        }

        public void setValue(Integer[] values) throws CAException {
            if (pv.getDimensionSize(0) != values.length) {
                throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
            }

            if (!pv.getType().isINT()) {
                throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
            }
            int[] newValues = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                newValues[i] = values[i];
            }
            CAStatus status = pv.write(new DBR_Int(newValues), null);
            if (status != CAStatus.NORMAL) {
                throw new CAStatusException(status);
            }

        }

        public void setValue(Float value) throws CAException {
            setValue(new Float[]{value});
        }

        public void setValue(Float[] values) throws CAException {
            if (pv.getDimensionSize(0) != values.length) {
                throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
            }

            if (!pv.getType().isFLOAT()) {
                throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
            }
            float[] newValues = new float[values.length];

            for (int i = 0; i < values.length; i++) {
                newValues[i] = values[i];
            }
            CAStatus status = pv.write(new DBR_Float(newValues), null);
            if (status != CAStatus.NORMAL) {
                throw new CAStatusException(status);
            }

        }

        public void setValue(Double value) throws CAException {
            setValue(new Double[]{value});
        }

        public void setValue(Double[] values) throws CAException {
            if (pv.getDimensionSize(0) != values.length) {
                throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
            }

            if (!pv.getType().isDOUBLE()) {
                throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
            }
            double[] newValues = new double[values.length];

            for (int i = 0; i < values.length; i++) {
                newValues[i] = values[i];
            }
            CAStatus status = pv.write(new DBR_Double(newValues), null);
            if (status != CAStatus.NORMAL) {
                throw new CAStatusException(status);
            }

        }

        public void setValue(String value) throws CAException {
            setValue(new String[]{value});
        }

        public void setValue(String[] values) throws CAException {
            if (pv.getDimensionSize(0) != values.length) {
                throw new IllegalArgumentException("Incorrect number of values. Expected: " + pv.getDimensionSize(0) + ", got: " + values.length);
            }

            if (!pv.getType().isSTRING()) {
                throw new IllegalArgumentException("Trying to write a " + values[0].getClass().getName() + " value in a " + pv.getType().getName() + " field.");
            }

            CAStatus status = pv.write(new DBR_String(values), null);
            if (status != CAStatus.NORMAL) {
                throw new CAStatusException(status);
            }

        }

        public DBR getValue() throws CAException {
            if (pv == null) {
                throw new RuntimeException("Channel not initialized");
            }
            DBR dbr;
            if (pv.getType().isINT()) {
                dbr = new DBR_TIME_Int(pv.getDimensionSize(0));
            } else if (pv.getType().isFLOAT()) {
                dbr = new DBR_TIME_Float(pv.getDimensionSize(0));
            } else if (pv.getType().isDOUBLE()) {
                dbr = new DBR_TIME_Double(pv.getDimensionSize(0));
            } else if (pv.getType().isSTRING()) {
                dbr = new DBR_TIME_String(pv.getDimensionSize(0));
            } else {
                throw new RuntimeException("Channel incorrectly initialized");
            }
            CAStatus status = pv.read(dbr, null);
            if (status != CAStatus.NORMAL) {
                throw new CAStatusException(status);
            }
            return dbr;
        }
    }

    public Channel createIntegerChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.pv.getType().isINT()) {
                return ch;
            } else {
                throw new RuntimeException("Channel " + name + " already exists, but is not of type Integer");
            }
        }
        MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_Int.TYPE, new int[length]);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    public Channel createFloatChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.pv.getType().isFLOAT()) {
                return ch;
            } else {
                throw new RuntimeException("Channel " + name + " already exists, but is not of type Float");
            }
        }
        MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_Float.TYPE, new float[length]);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    public Channel createDoubleChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.pv.getType().isDOUBLE()) {
                return ch;
            } else {
                throw new RuntimeException("Channel " + name + " already exists, but is not of type Double");
            }
        }
        MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_Double.TYPE, new double[length]);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    public Channel createStringChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.pv.getType().isSTRING()) {
                return ch;
            } else {
                throw new RuntimeException("Channel " + name + " already exists, but is not of type String");
            }
        }
        String[] array = new String[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = "";
        }
        MemoryProcessVariable pv = server.createMemoryProcessVariable(name, DBR_String.TYPE, array);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    public void destroyChannel(String name) {
        destroyChannel(channels.get(name));
    }

    public void destroyChannel(Channel ch) {
        channels.remove(ch.pv.getName());
        server.unregisterProcessVaribale(ch.pv.getName());
        ch.pv.destroy();
        ch.pv = null;
    }

    /**
     * Destroys the jca context and waits for the thread to return.
     *
     * @throws CAException
     * @throws java.lang.IllegalStateException
     *                     if the context has already been destroyed.
     */
    public void stop() throws CAException {
        for (String name : channels.keySet()) {
            Channel ch = channels.get(name);
            server.unregisterProcessVaribale(name);
            ch.pv.destroy();
        }
        channels.clear();
        executor.shutdown();
        serverContext.destroy();
        channels = null;
        server = null;
        serverContext = null;
    }


    /**
     * Main method for starting the server from the command line.
     *
     * @param args number of milliseconds to run the server
     */
    public static void main(String[] args) {
        Cas giapicas = new Cas();
        try {

            giapicas.start();
            //giapicas.addVariable("test", -1);

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        try {
            Thread.sleep((args[0] != null) ? Integer.parseInt(args[0]) : 10);
            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
