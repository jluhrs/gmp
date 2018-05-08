package edu.gemini.epics.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.api.DbrUtil;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.ConnectionEvent;
import gov.aps.jca.event.ConnectionListener;
import gov.aps.jca.event.ContextExceptionEvent;
import gov.aps.jca.event.ContextExceptionListener;
import gov.aps.jca.event.ContextVirtualCircuitExceptionEvent;
import gov.aps.jca.event.GetEvent;
import gov.aps.jca.event.GetListener;
import gov.aps.jca.event.MonitorEvent;
import gov.aps.jca.event.MonitorListener;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Support class that links an epicsClient with the JCA plumbing required to
 * get data updates for the changes
 */
public class ChannelBindingSupport {
    private static final Logger LOG = Logger.getLogger(ChannelBindingSupport.class.getName());

    private final Context _ctx;
    private final EpicsClient _epicsClient;
    private final Set<Channel> _channels = Sets.newHashSet();
    private boolean _closed;

    private final ConnectionListener connectionListener = new ConnectionListener() {
        @Override
        public void connectionChanged(ConnectionEvent ce) {
            Channel ch = (Channel) ce.getSource();
            if (ce.isConnected()) {
                addMonitorListenerToChannel(ch);
            } else {
                updateClientUponClosing(ch);

                // Now throw the dead channel away and reconnect. The old monitor
                // will get GC'd so we don't need to worry about it.
                if (!_closed) {
                    reconnectChannel(ch);
                }
            }
        }

        private void reconnectChannel(Channel ch) {
            LOG.warning("Connection was closed for " + ch.getName());
            try {
                LOG.info("Destroying channel " + ch.getName() + ", state is " + ch.getConnectionState());
                ch.destroy();

                LOG.info("Reconnecting channel " + ch.getName());
                ch = ch.getContext().createChannel(ch.getName(), this);
                ch.getContext().flushIO();

            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Trouble reconnecting channel " + ch.getName(), e);
            }
        }

        private void addMonitorListenerToChannel(Channel ch) {
            LOG.fine("Channel was opened for " + ch.getName());
            try {
                ch.addMonitor(Monitor.VALUE, monitorListener);
                ch.getContext().flushIO();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Could not add monitor to " + ch.getName(), e);
            }
        }

        private void updateClientUponClosing(Channel ch) {
            // The channel was _closed.
            // First, update the value to null.
            _epicsClient.valueChanged(ch.getName(), null);
            _epicsClient.disconnected();
        }
    };

    private final MonitorListener monitorListener = me -> {
        Channel ch = (Channel) me.getSource();
        try {
            distributeChannelValue(ch);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not request value for " + ch.getName(), e);
        }
    };

    private void distributeChannelValue(Channel ch) throws CAException {
        if (ch.getConnectionState() == Channel.CONNECTED) {
            ch.get(getListener);
            ch.getContext().flushIO();
        } else {
            // This can happen when a channel is closing. We can safely ignore this event.
            LOG.info("Discarding monitor change event from _closed channel: " + ch.getName());
        }
    }

    private final GetListener getListener = new GetListener() {
        @Override
        public void getCompleted(GetEvent ge) {
            Channel ch = (Channel) ge.getSource();
            // Wrap the call to avoid rogue clients to break it
            try {
                sendUpdateToClient(ge, ch);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Could not get/set value for " + ch.getName(), e);
            }
        }

        private void sendUpdateToClient(GetEvent ge, Channel ch) {
            // Get the new value.
            List<?> values = DbrUtil.extractValues(ge.getDBR());

            //send the value directly to the _epicsClient.
            _epicsClient.valueChanged(ch.getName(), values);
        }
    };

    public ChannelBindingSupport(Context ctx, EpicsClient epicsClient) throws EpicsException {
        Preconditions.checkArgument(ctx != null, "JCA Context cannot be null");
        Preconditions.checkArgument(epicsClient != null, "EpicsClient cannot be null");
        this._epicsClient = epicsClient;
        this._ctx = ctx;
        try {
            addContextListeners();
        } catch (CAException e) {
            throw new EpicsException("Exception while adding context listeners", e);
        }
    }

    private void addContextListeners() throws CAException {
        _ctx.addContextExceptionListener(new ContextExceptionListener() {
            public void contextException(ContextExceptionEvent cee) {
                LOG.log(Level.WARNING, "Trouble in JCA Context: " + cee.getMessage());
            }

            public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent cvce) {
                LOG.log(Level.WARNING, "Trouble in JCA Context: " + cvce);
            }
        });
        _ctx.addContextMessageListener(cme -> LOG.info(cme.getMessage()));
    }

    public void bindChannel(String channel) throws EpicsException {
        try {
            _channels.add(_ctx.createChannel(channel, connectionListener));
            _ctx.flushIO();
        } catch (CAException e) {
            throw new EpicsException("Exception while adding a new channel", e);
        }
    }

    public void close() throws EpicsException {
        _closed = true;
        for (Channel channel : _channels) {
            try {
                channel.destroy();
            } catch (IllegalStateException ise) {
                ise.printStackTrace();
                // Ok; channel already destroyed.
            } catch (CAException e) {
                e.printStackTrace();
                // Ok; channel already destroyed.
            }
        }
        _channels.clear();
        LOG.fine("Closed channel binder. " + Arrays.toString(_ctx.getChannels()) + " channel(s) remaining in context.");
    }

}