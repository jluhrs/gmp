package edu.gemini.epics.impl;

import edu.gemini.epics.IEpicsClient;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.Monitor;
import gov.aps.jca.event.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChannelBindingSupport {

    private static final Logger LOGGER = Logger.getLogger(ChannelBindingSupport.class.getName());
    private final Context _ctx;

    private final IEpicsClient target;
    private final Set<Channel> channels = new HashSet<Channel>();
    private boolean closed;

    private final ConnectionListener connectionListener = new ConnectionListener() {
        public void connectionChanged(ConnectionEvent ce) {
            Channel ch = (Channel) ce.getSource();
            if (ce.isConnected()) {

                LOGGER.fine("Channel was opened for " + ch.getName());
                try {
                    ch.addMonitor(Monitor.VALUE, monitorListener);
                    ch.getContext().flushIO();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Could not add monitor to " + ch.getName(), e);
                }

            } else {

                // The channel was closed.
                // First, update the value to null.
                target.channelChanged(ch.getName(), null);

                // Now throw the dead channel away and reconnect. The old monitor
                // will get GC'd so we don't need to worry about it.
                if (!closed) {
                    LOGGER.warning("Connection was closed for " + ch.getName());
                    try {

                        LOGGER.info("Destroying channel " + ch.getName() + ", state is " + ch.getConnectionState());
                        ch.destroy();

                        LOGGER.info("Reconnecting channel " + ch.getName());
                        ch = ch.getContext().createChannel(ch.getName(), this);
                        ch.getContext().flushIO();

                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Trouble reconnecting channel " + ch.getName(), e);
                    }
                }
            }
        }
    };

    private final MonitorListener monitorListener = new MonitorListener() {
        public void monitorChanged(MonitorEvent me) {
            Channel ch = (Channel) me.getSource();
            try {
                if (ch.getConnectionState() == Channel.CONNECTED) {
                    ch.get(getListener);
                    ch.getContext().flushIO();
                } else {
                    // This can happen when a channel is closing. We can safely ignore this event.
                    LOGGER.info("Discarding monitor change event from closed channel: " + ch.getName());
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not request value for " + ch.getName(), e);
            }
        }
    };


    private final GetListener getListener = new GetListener() {
        public void getCompleted(GetEvent ge) {
            Channel ch = (Channel) ge.getSource();
            try {

                // Get the new value.
                Object value = ge.getDBR().getValue();

                //send the value directly to the target. 
                target.channelChanged(ch.getName(), value);

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not get/set value for " + ch.getName(), e);
            }
        }
    };


    public ChannelBindingSupport(Context ctx, IEpicsClient target) throws CAException {
        this.target = target;
        this._ctx = ctx;
        if (_ctx == null) {
            _ctx.addContextExceptionListener(new ContextExceptionListener() {
                public void contextException(ContextExceptionEvent cee) {
                    LOGGER.log(Level.WARNING, "Trouble in JCA Context.", cee);
                }

                public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent cvce) {
                    LOGGER.log(Level.WARNING, "Trouble in JCA Context.", cvce);
                }
            });
            _ctx.addContextMessageListener(new ContextMessageListener() {
                public void contextMessage(ContextMessageEvent cme) {
                    LOGGER.info(cme.getMessage());
                }
            });
        }
    }

    public void bindChannel(String channel) throws CAException {
        channels.add(_ctx.createChannel(channel, connectionListener));
        _ctx.flushIO();
    }

    public void close() throws IllegalStateException, CAException {
        closed = true;
        for (Iterator<Channel> it = channels.iterator(); it.hasNext();) {
            Channel ch = it.next();
            try {
                ch.destroy();
            } catch (IllegalStateException ise) {
                // Ok; channel already destroyed.
            }
            it.remove();
        }
        LOGGER.info("Closed channel binder. " + _ctx.getChannels().length + " channel(s) remaining in context.");
    }

    public void destroy() {
        try {
            if (_ctx != null) {
                _ctx.destroy();
                LOGGER.info("Destroyed JCA context.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not destroy JCA context.", e);
        }
    }

}


