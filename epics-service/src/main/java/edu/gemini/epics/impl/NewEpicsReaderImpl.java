package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import edu.gemini.epics.ClientEpicsChannel;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.JCAContextController;
import edu.gemini.epics.NewEpicsReader;
import edu.gemini.epics.api.ReadOnlyChannel;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.event.*;

import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class NewEpicsReaderImpl
 *
 * @author Nicolas A. Barriga
 *         Date: 11/7/11
 */
public class NewEpicsReaderImpl implements NewEpicsReader {
    private static final Logger LOG = Logger.getLogger(EpicsBaseImpl.class.getName());
    private final CAJContext _ctx;
    private final ConcurrentMap<String, ClientEpicsChannel<?>> _channels = Maps.newConcurrentMap();

    public NewEpicsReaderImpl(JCAContextController epicsService) {
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

    public ClientEpicsChannel<Double> getDoubleChannel(String channelName) {
        ClientEpicsChannel<Double> ch;
        if (_channels.containsKey(channelName)) {
            ch = (ClientEpicsChannel<Double>) _channels.get(channelName);
            if (!ch.isValid()) {
                throw new IllegalStateException("Channel exists, but is not connected");
            }
            if (!ch.getType().isDOUBLE()) {
                throw new IllegalArgumentException("Channel " + channelName + " already exists, but is of incorrect type.");
            }
        } else {
            CAJChannel cajChannel = bindChannel(channelName);
            if (!cajChannel.getFieldType().isDOUBLE()) {
                try {
                    cajChannel.destroy();
                } catch (CAException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                }
                throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
            } else {
                ch = new EpicsChannel<Double>(cajChannel);
            }
            _channels.put(channelName, ch);
        }
        return ch;
    }

    public ClientEpicsChannel<Integer> getIntegerChannel(String channelName) {
        ClientEpicsChannel<Integer> ch;
        if (_channels.containsKey(channelName)) {
            ch = (ClientEpicsChannel<Integer>) _channels.get(channelName);
            if (!ch.isValid()) {
                throw new IllegalStateException("Channel exists, but is not connected");
            }
            if (!ch.getType().isINT()) {
                throw new IllegalArgumentException("Channel " + channelName + " already exists, but is of incorrect type.");
            }
        } else {
            CAJChannel cajChannel = bindChannel(channelName);
            if (!cajChannel.getFieldType().isINT()) {
                try {
                    cajChannel.destroy();
                } catch (CAException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                }
                throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
            } else {
                ch = new EpicsChannel<Integer>(cajChannel);
            }
            _channels.put(channelName, ch);
        }
        return ch;
    }

    @Override
    public ClientEpicsChannel<Float> getFloatChannel(String channelName) {
        ClientEpicsChannel<Float> ch;
        if (_channels.containsKey(channelName)) {
            ch = (ClientEpicsChannel<Float>) _channels.get(channelName);
            if (!ch.isValid()) {
                throw new IllegalStateException("Channel exists, but is not connected");
            }
            if (!ch.getType().isFLOAT()) {
                throw new IllegalArgumentException("Channel " + channelName + " already exists, but is of incorrect type.");
            }
        } else {
            CAJChannel cajChannel = bindChannel(channelName);
            if (!cajChannel.getFieldType().isFLOAT()) {
                try {
                    cajChannel.destroy();
                } catch (CAException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                }
                throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
            } else {
                ch = new EpicsChannel<Float>(cajChannel);
            }
            _channels.put(channelName, ch);
        }
        return ch;
    }

    @Override
    public ClientEpicsChannel<String> getStringChannel(String channelName) {
        ClientEpicsChannel<String> ch;
        if (_channels.containsKey(channelName)) {
            ch = (ClientEpicsChannel<String>) _channels.get(channelName);
            if (!ch.isValid()) {
                throw new IllegalStateException("Channel exists, but is not connected");
            }
            if (!ch.getType().isSTRING()) {
                throw new IllegalArgumentException("Channel " + channelName + " already exists, but is of incorrect type.");
            }
        } else {
            CAJChannel cajChannel = bindChannel(channelName);
            if (!cajChannel.getFieldType().isSTRING()) {
                try {
                    cajChannel.destroy();
                } catch (CAException e) {
                    LOG.log(Level.WARNING, e.getMessage(), e);
                }
                throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
            } else {
                ch = new EpicsChannel<String>(cajChannel);
            }
            _channels.put(channelName, ch);
        }
        return ch;
    }

    @Override
    public ClientEpicsChannel<?> getChannelAsync(String channelName) {
        ClientEpicsChannel<?> ch;
        if (_channels.containsKey(channelName)) {
            ch = _channels.get(channelName);
        } else {
            ch = new EpicsChannel(bindChannelAsync(channelName, null));
            _channels.put(channelName, ch);
        }
        return ch;
    }

    @Override
    public void destroyChannel(ClientEpicsChannel<?> channel) {
        _channels.remove(channel.getName());
        channel.destroy();

    }

    private synchronized CAJChannel bindChannelAsync(String channel, ConnectionListener listener) throws EpicsException {
        try {
            return addNewChannel(channel, true, listener);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            throw new EpicsException("Epics channel in incorrect state " + channel, e);
        }
    }

    private synchronized CAJChannel bindChannel(String channel) throws EpicsException {
        try {
            return addNewChannel(channel, false, null);
        } catch (CAException e) {
            throw new EpicsException("Problem on Channel Access", e);
        } catch (TimeoutException e) {
            throw new EpicsException("Timeout while binding to epics channel " + channel, e);
        } catch (IllegalStateException e) {
            throw new EpicsException("Epics channel in incorrect state " + channel, e);
        }
    }

    private CAJChannel addNewChannel(String channelName, boolean async, ConnectionListener listener) throws CAException, TimeoutException {
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

        return epicsChannel;
    }
}
