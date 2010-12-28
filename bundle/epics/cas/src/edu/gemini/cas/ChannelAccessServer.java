package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
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
 * Class ChannelAccessServer. Implements the bulk of the giapi-cas bundle.
 * <p/>
 * It implements the Runnable interface because we need to run the JCA server in another thread.
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class ChannelAccessServer implements IChannelAccessServer {
    private static final Logger LOG = Logger.getLogger(ChannelAccessServer.class.getName());
    private DefaultServerImpl server;
    private ServerContext serverContext = null;
    private ExecutorService executor;
    private final JCALibrary jca = JCALibrary.getInstance();
    private Map<String, Channel> channels;

    /**
     * Constructor.
     */
    public ChannelAccessServer() {
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
            throw new IllegalStateException("Tried to start the ChannelAccessServer more than once");
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

    @Override
    public IChannel createIntegerChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.getType().isINT()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Integer");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Int.TYPE,new int[length]);
        server.registerProcessVaribale(name, pv);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IChannel createFloatChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.getType().isFLOAT()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Float");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Float.TYPE,new float[length]);
        server.registerProcessVaribale(name, pv);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IChannel createDoubleChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.getType().isDOUBLE()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Double");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Double.TYPE,new double[length]);
        server.registerProcessVaribale(name, pv);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IChannel createStringChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.getType().isSTRING()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type String");
            }
        }
        String[] array = new String[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = "";
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_String.TYPE,array);
        server.registerProcessVaribale(name, pv);
        Channel ch = new Channel(pv);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Removes channel from internal Map, unregisters from server and destroys PV
     *
     * @param channel the IChannel to remove
     */
    @Override
    public void destroyChannel(IChannel channel) {
        Channel ch=null;
        try{
            ch = channels.get(channel.getName());
        }catch(NullPointerException ex){//if channel was already destroyed
            return;        
        }
        channels.remove(ch.getName());
        server.unregisterProcessVaribale(ch.getName());
        ch.destroy();
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
            ch.destroy();
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
        ChannelAccessServer giapicas = new ChannelAccessServer();
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
