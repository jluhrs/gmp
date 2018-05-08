package edu.gemini.cas.impl;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import com.google.common.collect.ImmutableList;
import edu.gemini.cas.*;
import edu.gemini.epics.api.ReadOnlyChannel;
import gov.aps.jca.*;
import gov.aps.jca.cas.ServerContext;

import java.lang.Enum;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class ChannelAccessServerImpl. Implements the bulk of the giapi-cas bundle.
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class ChannelAccessServerImpl implements ChannelAccessServer {
    private static final Logger LOG = Logger.getLogger(ChannelAccessServerImpl.class.getName());
    private DefaultServerImpl server;
    private ServerContext serverContext = null;
    private ExecutorService executor;
    private final JCALibrary jca = JCALibrary.getInstance();
    private Map<String, edu.gemini.epics.api.Channel<?>> channels;

    /**
     * Constructor.
     */
    public ChannelAccessServerImpl() {
    }

    /**
     * Creates a server, a jca context and spawns a new thread to run the server.
     *
     * @throws IllegalStateException if trying to start an already started server
     * @throws CAException           is thrown if the jca context could not be instantiated.
     */
    public void start() throws CAException {
        executor = Executors.newSingleThreadExecutor();
        channels = new HashMap<>();
        server = new DefaultServerImpl();
        if (serverContext != null) {
            throw new IllegalStateException("Tried to start the ChannelAccessServerImpl more than once");
        }
        serverContext = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server);
        executor.execute(() -> {
            try {
                serverContext.run(0);
            } catch (IllegalStateException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            } catch (CAException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        });
    }

    /**
     * Destroys the jca context and waits for the thread to return.
     *
     * @throws CAException if there are problems with Channel Access
     * @throws java.lang.IllegalStateException
     *                     if the context has already been destroyed.
     */
    public void stop() throws CAException {
        for (String name : channels.keySet()) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof AbstractChannel) {
                ((AbstractChannel) ch).destroy(server);
            } else if (ch instanceof AbstractAlarmChannel) {
                ((AbstractAlarmChannel) ch).destroy(server);
            } else {
                LOG.warning("Unknown channel type: " + ch.getClass().getName());
            }
        }
        channels.clear();
        executor.shutdown();
        serverContext.destroy();
        channels = null;
        server = null;
        serverContext = null;
    }

    @Override
    public <T> edu.gemini.epics.api.Channel<T> createChannel(String name, T value) throws CAException {
        return createChannel(name, ImmutableList.of(value));
    }

    @Override
    public <T> edu.gemini.epics.api.Channel<T> createChannel(String name, List<T> values) throws CAException {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("At least one value must be passed");
        }
        ServerChannel ch;
        if (values.get(0) instanceof Integer) {
            ch = createIntegerChannel(name, values.size());
        } else if (values.get(0) instanceof Short) {
            ch = createShortChannel(name, values.size());
        } else if (values.get(0) instanceof Float) {
            ch = createFloatChannel(name, values.size());
        } else if (values.get(0) instanceof Double) {
            ch = createDoubleChannel(name, values.size());
        } else if (values.get(0) instanceof String) {
            ch = createStringChannel(name, values.size());
        } else if (values.get(0) instanceof Byte) {
            ch = createByteChannel(name, values.size());
        } else if (values.get(0) instanceof Enum) {
            Class<? extends Enum> clazz = (Class<? extends Enum>) values.get(0).getClass();
            ch = createEnumChannel(name, values.size(), clazz);
        } else {
            throw new IllegalArgumentException("Unsupported item type " + values.get(0).getClass());
        }
        ch.setValue(values);
        return ch;
    }

    @Override
    public <T> AlarmChannel<T> createAlarmChannel(String name, T value) throws CAException {
        return createAlarmChannel(name, ImmutableList.of(value));
    }

    @Override
    public <T> AlarmChannel<T> createAlarmChannel(String name, List<T> values) throws CAException {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("At least one value must be passed");
        }
        AlarmChannel ch = null;
        if (values.get(0) instanceof Integer) {
            ch = createIntegerAlarmChannel(name, values.size());
        } else if (values.get(0) instanceof Float) {
            ch = createFloatAlarmChannel(name, values.size());
        } else if (values.get(0) instanceof Double) {
            ch = createDoubleAlarmChannel(name, values.size());
        } else if (values.get(0) instanceof String) {
            ch = createStringAlarmChannel(name, values.size());
        } else if (values.get(0) instanceof Enum) {
            Class<? extends Enum> clazz = (Class<? extends Enum>) values.get(0).getClass();
            ch = createEnumAlarmChannel(name, values.size(), clazz);
        } else {
            throw new IllegalArgumentException("Unsupported item type " + values.get(0).getClass());
        }
        ch.setValue(values);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Integer
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private ServerChannel<Integer> createIntegerChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof IntegerChannel) {
                return (IntegerChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Integer");
            }
        }
        IntegerChannel ch = new IntegerChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Short
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private ServerChannel<Short> createShortChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof ShortChannel) {
                return (ShortChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Short");
            }
        }
        ShortChannel ch = new ShortChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Float
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private ServerChannel<Float> createFloatChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof FloatChannel) {
                return (FloatChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Float");
            }
        }
        FloatChannel ch = new FloatChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Byte
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private ServerChannel<Byte> createByteChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof ByteChannel) {
                return (ByteChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Float");
            }
        }
        ByteChannel ch = new ByteChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Double
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private ServerChannel<Double> createDoubleChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof DoubleChannel) {
                return (DoubleChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type Double");
            }
        }
        DoubleChannel ch = new DoubleChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type String
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private ServerChannel<String> createStringChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof StringChannel) {
                return (StringChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type String");
            }
        }
        StringChannel ch = new StringChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Enum
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @param clazz  the enum class
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private <T extends Enum<T>> ServerChannel<T> createEnumChannel(String name, int length, Class<T> clazz) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof EnumChannel && ((EnumChannel) ch).getEnumClass().equals(clazz)) {
                return (EnumChannel<T>) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type EnumChannel<" + clazz.getSimpleName() + ">");
            }
        }
        EnumChannel<T> ch = new EnumChannel<T>(name, length, clazz);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Integer,
     * that is able to raise an alarm.
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private AlarmChannel<Integer> createIntegerAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof IntegerAlarmChannel) {
                return (IntegerAlarmChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type IntegerAlarmChannel");
            }
        }
        IntegerAlarmChannel ch = new IntegerAlarmChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Float,
     * that is able to raise an alarm.
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private AlarmChannel<Float> createFloatAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof FloatAlarmChannel) {
                return (FloatAlarmChannel) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type FloatAlarmChannel");
            }
        }
        FloatAlarmChannel ch = new FloatAlarmChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Double,
     * that is able to raise an alarm.
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private AlarmChannel<Double> createDoubleAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof DoubleAlarmChannel) {
                return (DoubleAlarmChannel) ch;

            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type DoubleAlarmChannel");
            }
        }
        DoubleAlarmChannel ch = new DoubleAlarmChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type String,
     * that is able to raise an alarm.
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private AlarmChannel<String> createStringAlarmChannel(String name, int length) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof StringAlarmChannel) {
                return (StringAlarmChannel) ch;

            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type StringAlarmChannel");
            }
        }
        StringAlarmChannel ch = new StringAlarmChannel(name, length);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Creates a new channel, with a simulated EPICS process variable(PV) of type Enum,
     * that is able to raise an alarm.
     *
     * @param name   name of the PV in EPICS
     * @param length length of the PV data
     * @param clazz  the enum class
     * @return the new channel
     * @throws IllegalArgumentException if channel already exists but is of different type
     */
    private <T extends Enum<T>> AlarmChannel<T> createEnumAlarmChannel(String name, int length, Class<T> clazz) {
        if (channels.containsKey(name)) {
            edu.gemini.epics.api.Channel ch = channels.get(name);
            if (ch instanceof EnumAlarmChannel && ((EnumAlarmChannel) ch).getEnumClass().equals(clazz)) {
                return (EnumAlarmChannel<T>) ch;
            } else {
                throw new IllegalArgumentException("Channel " + name + " already exists, but is not of type EnumAlarmChannel<" + clazz.getSimpleName() + ">");
            }
        }
        EnumAlarmChannel<T> ch = new EnumAlarmChannel<T>(name, length, clazz);
        ch.register(server);
        channels.put(name, ch);
        return ch;
    }

    /**
     * Removes channel from internal Map, unregisters from server and destroys PV
     *
     * @param channel the Channel to remove
     */
    @Override
    public void destroyChannel(ReadOnlyChannel<?> channel) {
        edu.gemini.epics.api.Channel ch;
        try {
            ch = channels.get(channel.getName());
        } catch (NullPointerException ex) {//if channel was already destroyed
            return;
        }
        if (ch != null) {
            channels.remove(ch.getName());
            if (ch instanceof AbstractChannel) {
                ((AbstractChannel) ch).destroy(server);
            } else if (ch instanceof AbstractAlarmChannel) {
                ((AbstractAlarmChannel) ch).destroy(server);
            } else {
                LOG.warning("Unknown channel type: " + ch.getClass().getName());
            }
        }
    }


    /**
     * Main method for starting the server from the command line.
     *
     * @param args number of milliseconds to run the server
     */
    public static void main(String[] args) {
        ChannelAccessServerImpl giapicas = new ChannelAccessServerImpl();
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
