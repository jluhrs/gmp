package edu.gemini.epics.impl;

import edu.gemini.epics.IEpicsWriter;
import edu.gemini.epics.EpicsException;
import gov.aps.jca.*;
import gov.aps.jca.event.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the EpicsWritter interface using JCA
 */
public class EpicsWriter implements IEpicsWriter {


    private static final Logger LOG = Logger.getLogger(EpicsWriter.class.getName());

    public static Context ctx;

    private Map<String, Channel> _channels = new TreeMap<String, Channel>();

    private boolean closed = false;

    //TODO: The following code can be resucitated when more testing is done to define
    //      how to reconnect correctly in case of EPICS trouble. 

    private final ConnectionListener _connectionListener = new ConnectionListener() {
        public void connectionChanged(ConnectionEvent ce) {
            Channel ch = (Channel) ce.getSource();
            LOG.fine("Channel was opened for " + ch.getName());

            if (ce.isConnected()) {
                LOG.fine("Channel was opened for " + ch.getName());


            } else {

                // Now throw the dead channel away and reconnect.
                if (!closed) {
                    LOG.warning("Connection was closed for " + ch.getName());
                    try {
                        LOG.info("Destroying channel " + ch.getName() + ", state is " + ch.getConnectionState());
                        ch.destroy();

                        LOG.info("Reconnecting channel " + ch.getName());
                        ch = ch.getContext().createChannel(ch.getName(), this);
                        _channels.put(ch.getName(), ch);
                        ch.getContext().flushIO();
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Trouble reconnecting channel " + ch.getName(), e);
                    }
                }
            }
        }
    };


    public EpicsWriter() throws CAException {
        if (ctx == null) {
            ctx = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            ctx.addContextExceptionListener(new ContextExceptionListener() {
                public void contextException(ContextExceptionEvent cee) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context.", cee);
                }

                public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent cvce) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context.", cvce);
                }
            });
            ctx.addContextMessageListener(new ContextMessageListener() {
                public void contextMessage(ContextMessageEvent cme) {
                    LOG.info(cme.getMessage());
                }
            });
        }

    }


    public void bindChannel(String channel) throws EpicsException {
        try {
            Channel cnl = ctx.createChannel(channel);
            //TODO: Do we need to bind the channels asynchronously, using the connection listener?
            _channels.put(channel, cnl);
            ctx.pendIO(5.0);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            LOG.log(Level.WARNING, "Epics channel in incorrect state " + channel, e);
        }

    }

    private Channel getChannel(String channelName) {
        Channel channel = _channels.get(channelName);
        if (channel == null) {
            LOG.log(Level.FINE, "No information available about channel " + channelName);
        }
        return channel;
    }


    @Override
    public void write(String channelName, Double value) throws EpicsException{
        Channel channel = getChannel(channelName);
        if (channel == null) {
            return;
        }

        try {
            channel.put(value);
            channel.getContext().flushIO();
        } catch (CAException e) {
            throw new EpicsException("Problem writting to channel " + channelName, e);
        }
    }

    @Override
    public void write(String channelName, Double[] value) throws EpicsException {

        double val[] = new double[value.length];
        for (int i = 0; i < val.length; i++) {
            val[i] = value[i];
        }
        write(channelName, val);
    }

    @Override
    public void write(String channelName, double[] value)  throws EpicsException {
        Channel channel = getChannel(channelName);

        if (channel == null) {
            return;
        }
        try {
            channel.put(value);
            channel.getContext().flushIO();
        } catch (CAException e) {
            throw new EpicsException("Problem writting to channel " + channelName, e);
        }
    }


    public void close() throws IllegalStateException, CAException {
        closed = true;
        for (String channelName : _channels.keySet()) {
            Channel ch = _channels.get(channelName);
            try {
                ch.destroy();
            } catch (IllegalStateException ise) {
                // Ok; channel already destroyed.
            }
        }
        LOG.info("Closed channel binder. " + ctx.getChannels().length + " channel(s) remaining in context.");
    }


    public static void destroy() {
        try {
            if (ctx != null) {
                ctx.destroy();
                LOG.info("Destroyed JCA context.");
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Could not destroy JCA context.", e);
        }
        ctx = null;
    }
}
