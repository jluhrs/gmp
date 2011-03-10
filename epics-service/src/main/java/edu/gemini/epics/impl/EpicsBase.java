package edu.gemini.epics.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.IEpicsBase;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.ContextExceptionEvent;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextMessageEvent;
import gov.aps.jca.event.ContextMessageListener;
import gov.aps.jca.event.ContextVirtualCircuitExceptionEvent;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for Epics Accessing Objects
 */
public class EpicsBase implements IEpicsBase {
    private static final Logger LOG = Logger.getLogger(EpicsBase.class.getName());

    private final Context _ctx;
    private final Map<String, Channel> _channels = Maps.newTreeMap();
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


    public EpicsBase(Context ctx) {
        Preconditions.checkArgument(ctx != null, "Passed JCA Context cannot be null");
        this._ctx = ctx;

        try {
            addJCAContextListeners();
        } catch (CAException e) {
            throw new EpicsException("Caught exception while adding JCA Context listeners", e);
        }

    }

    private void addJCAContextListeners() throws CAException {
        _ctx.addContextExceptionListener(new ContextExceptionListener() {
                public void contextException(ContextExceptionEvent cee) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context.", cee);
                }

                public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent cvce) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context.", cvce);
                }
            });
        _ctx.addContextMessageListener(new ContextMessageListener() {
            public void contextMessage(ContextMessageEvent cme) {
                LOG.info(cme.getMessage());
            }
        });
    }

    public void bindChannel(String channel) throws EpicsException {
        try {
            bindNewChannel(channel);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            LOG.log(Level.WARNING, "Epics channel in incorrect state " + channel, e);
        }
    }

    private void bindNewChannel(String channelName) throws CAException, TimeoutException {
        Channel epicsChannel = _ctx.createChannel(channelName);
        //TODO: Do we need to bind the channels asynchronously, using the connection listener?
        _channels.put(channelName, epicsChannel);
        _ctx.pendIO(5.0);
    }

    protected Channel getChannel(String channelName) {
        Channel channel = _channels.get(channelName);
        if (channel == null) {
            LOG.log(Level.FINE, "No information available about channel " + channelName);
        }
        return channel;
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
        _channels.clear();
        LOG.info("Closed channel binder. " + Arrays.toString(_ctx.getChannels()) + " channel(s) remaining in context.");
    }

    protected boolean isChannelKnown(String channelName) {
        return _channels.containsKey(channelName);
    }
}
