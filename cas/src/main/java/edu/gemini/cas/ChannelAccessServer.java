package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import edu.gemini.cas.epics.AlarmMemoryProcessVariable;
import gov.aps.jca.*;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.*;
import org.apache.felix.ipojo.annotations.*;

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
@Component
@Instantiate
@Provides
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
    @Validate
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
            if (ch.isInteger()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Integer");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Int.TYPE,new int[length]);
        Channel ch = new Channel(pv);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IChannel createFloatChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.isFloat()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Float");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Float.TYPE,new float[length]);
        Channel ch = new Channel(pv);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IChannel createDoubleChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.isDouble()) {
                return ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Double");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Double.TYPE,new double[length]);
        Channel ch = new Channel(pv);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IChannel createStringChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if (ch.isString()) {
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
        Channel ch = new Channel(pv);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    @Override
    public IAlarmChannel createIntegerAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if(ch instanceof AlarmChannel){
                if (ch.isInteger()) {
                    return (AlarmChannel)ch;
                } else {
                    throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Integer");
                }
            }else{
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not an AlarmChannel");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Int.TYPE,new int[length]);
        AlarmMemoryProcessVariable alarmPV = new AlarmMemoryProcessVariable(name+".OMSS",null,DBR_String.TYPE,new String[]{""});
        AlarmChannel ch = new AlarmChannel(pv,alarmPV);
        ch.register(server);
        channels.put(name, ch);
        return ch;
     }

    @Override
    public IAlarmChannel createFloatAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if(ch instanceof AlarmChannel){
                if (ch.isFloat()) {
                    return (AlarmChannel)ch;
                } else {
                    throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Float");
                }
            }else{
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not an AlarmChannel");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Float.TYPE,new float[length]);
        AlarmMemoryProcessVariable alarmPV = new AlarmMemoryProcessVariable(name+".OMSS",null,DBR_String.TYPE,new String[]{""});
        AlarmChannel ch = new AlarmChannel(pv,alarmPV);
        ch.register(server);
        channels.put(name, ch);
        return ch;
     }

    @Override
    public IAlarmChannel createDoubleAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if(ch instanceof AlarmChannel){
                if (ch.isDouble()) {
                    return (AlarmChannel)ch;
                } else {
                    throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Double");
                }
            }else{
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not an AlarmChannel");
            }
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_Double.TYPE,new double[length]);
        AlarmMemoryProcessVariable alarmPV = new AlarmMemoryProcessVariable(name+".OMSS",null,DBR_String.TYPE,new String[]{""});
        AlarmChannel ch = new AlarmChannel(pv,alarmPV);
        ch.register(server);
        channels.put(name, ch);
        return ch;    }

    @Override
    public IAlarmChannel createStringAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            Channel ch = channels.get(name);
            if(ch instanceof AlarmChannel){
                if (ch.isString()) {
                    return (AlarmChannel)ch;
                } else {
                    throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type String");
                }
            }else{
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not an AlarmChannel");
            }
        }
        String[] array = new String[length];
        for (int i = 0; i < array.length; i++) {
            array[i] = "";
        }
        AlarmMemoryProcessVariable pv = new AlarmMemoryProcessVariable(name,null,DBR_String.TYPE,array);
        AlarmMemoryProcessVariable alarmPV = new AlarmMemoryProcessVariable(name+".OMSS",null,DBR_String.TYPE,new String[]{""});
        AlarmChannel ch = new AlarmChannel(pv,alarmPV);
        ch.register(server);
        channels.put(name, ch);
        return ch;    }

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
        if(ch!=null){
            channels.remove(ch.getName());
            ch.destroy(server);
        }
    }


    /**
     * Destroys the jca context and waits for the thread to return.
     *
     * @throws CAException
     * @throws java.lang.IllegalStateException
     *                     if the context has already been destroyed.
     */
    @Invalidate
    public void stop() throws CAException {
        for (String name : channels.keySet()) {
            Channel ch = channels.get(name);
            ch.destroy(server);
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
