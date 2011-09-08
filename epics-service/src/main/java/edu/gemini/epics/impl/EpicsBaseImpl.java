package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsBase;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.event.*;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for Epics Accessing Objects
 */
public class EpicsBaseImpl implements EpicsBase {
    private static final Logger LOG = Logger.getLogger(EpicsBaseImpl.class.getName());

    private final CAJContext _ctx;
    private final ConcurrentMap<String, CAJChannel> _channels = Maps.newConcurrentMap();

    public EpicsBaseImpl(JCAContextController epicsService) {
        Preconditions.checkArgument(epicsService != null, "Passed JCAContextController cannot be null");
        Preconditions.checkArgument(epicsService.getJCAContext() != null, "Passed JCA Context cannot be null");
        this._ctx = epicsService.getJCAContext();

        try {
            addJCAContextListeners();
        } catch (CAException e) {
            throw new EpicsException("Caught exception while adding JCA Context listeners", e);
        }

    }

    private void addJCAContextListeners() throws CAException {
        synchronized (_ctx) {
            _ctx.addContextExceptionListener(new ContextExceptionListener() {
                public void contextException(ContextExceptionEvent cee) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context.", cee);
                }

                public void contextVirtualCircuitException(ContextVirtualCircuitExceptionEvent cvce) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context: " + cvce.getVirtualCircuit() + " Status: " + cvce.getStatus());
                }
            });
            _ctx.addContextMessageListener(new ContextMessageListener() {
                public void contextMessage(ContextMessageEvent cme) {
                    LOG.info(cme.getMessage());
                }
            });
        }
    }

    @Override
    public synchronized void bindChannelAsync(String channel) throws EpicsException {
        try {
            bindNewChannel(channel, true, null);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            throw new EpicsException("Epics channel in incorrect state " + channel, e);
        }
    }

    @Override
    public boolean isChannelConnected(String channel) {
        return isChannelKnown(channel) && _channels.get(channel).getConnectionState() == Channel.ConnectionState.CONNECTED;
    }

    @Override
    public synchronized void bindChannelAsync(String channel, ConnectionListener listener) throws EpicsException {
        try {
            bindNewChannel(channel, true, listener);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            throw new EpicsException("Epics channel in incorrect state " + channel, e);
        }
    }

    @Override
    public synchronized void bindChannel(String channel) throws EpicsException {
        try {
            bindNewChannel(channel, false, null);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            throw new EpicsException("Epics channel in incorrect state " + channel, e);
        }
    }

    private void bindNewChannel(String channelName, boolean async, ConnectionListener listener) throws CAException, TimeoutException {
        synchronized (_ctx) {
            if (!isChannelKnown(channelName)) {
                addNewChannel(channelName, async, listener);
            }
        }
    }


    private void addNewChannel(String channelName, boolean async, ConnectionListener listener) throws CAException, TimeoutException {
        CAJChannel epicsChannel = null;
        if (async) {
            if (listener != null) {
                epicsChannel = (CAJChannel) _ctx.createChannel(channelName, listener);
            } else {
                epicsChannel = (CAJChannel) _ctx.createChannel(channelName, new ConnectionListener() {
                    @Override
                    public void connectionChanged(ConnectionEvent ev) {
                        //do nothing, this is just so the channel can be created asynchronously
                        //here we should make sure that the channel is closed and invalidated.
//                if(!ev.isConnected()) {
//                    EpicsBaseImpl.this.unbindChannel(channelName);
//                }
                    }
                });
            }
        } else {
            epicsChannel = (CAJChannel) _ctx.createChannel(channelName);
            _ctx.pendIO(1.0);
            if (epicsChannel.getConnectionState() != Channel.ConnectionState.CONNECTED) {
                throw new IllegalStateException("Channel " + channelName + " cannot be connected");
            }
        }
        _channels.putIfAbsent(channelName, epicsChannel);
    }

    @Override
    public synchronized void unbindChannel(String channelName) throws EpicsException {
        try {
            CAJChannel channel = _channels.remove(channelName);
            if (channel != null) {
                _ctx.destroyChannel(channel, false);
            }
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (IllegalStateException e) {
            throw new EpicsException("Epics channel in incorrect state " + channelName, e);
        }
    }

    protected Channel getChannel(String channelName) {
        Channel channel = null;
        if (isChannelKnown(channelName)) {
            channel = _channels.get(channelName);
        } else {
            LOG.log(Level.FINE, "No information available about channel " + channelName);
        }
        return channel;
    }

    public void close() {
        for (String channelName : _channels.keySet()) {
            Channel ch = _channels.get(channelName);
            try {
                ch.destroy();
            } catch (IllegalStateException ise) {
                // Ok; channel already destroyed.
            } catch (CAException e) {
                // Log and continue
                LOG.log(Level.WARNING, "Exception while closing channel " + ch, e);
            }
        }
        _channels.clear();
        LOG.info("Closed channel binder. " + Arrays.toString(_ctx.getChannels()) + " channel(s) remaining in context.");
    }

    protected boolean isChannelKnown(String channelName) {
        return _channels.containsKey(channelName);
    }
}
