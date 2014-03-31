package edu.gemini.epics.impl;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import com.google.common.base.Preconditions;
import edu.gemini.epics.ReadOnlyClientEpicsChannel;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.event.*;

//import java.lang.ref.PhantomReference;
//import java.lang.ref.Reference;
//import java.lang.ref.ReferenceQueue;
//import java.util.IdentityHashMap;
//import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsChannelFactory
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
class EpicsChannelFactory {
    private static final Logger LOG = Logger.getLogger(EpicsChannelFactory.class.getName());
    private final CAJContext _ctx;
//    private final ReferenceQueue<ReadWriteEpicsChannelImpl<?>> refQueue = new ReferenceQueue<ReadWriteEpicsChannelImpl<?>>();
//    private final IdentityHashMap<PhantomReference<ReadWriteEpicsChannelImpl<?>>, CAJChannel> ch2Ref = new IdentityHashMap<PhantomReference<ReadWriteEpicsChannelImpl<?>>, CAJChannel>();

    protected EpicsChannelFactory(JCAContextController epicsService) {
        Preconditions.checkArgument(epicsService != null, "Passed JCAContextController cannot be null");
        Preconditions.checkArgument(epicsService.getJCAContext() != null, "Passed JCA Context cannot be null");
        this._ctx = epicsService.getJCAContext();

        try {
            addJCAContextListeners();
        } catch (CAException e) {
            throw new EpicsException("Caught exception while adding JCA Context listeners", e);
        }

        //uncomment to enable autodestroy before garbage collecting the channel
//        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
//        executor.scheduleWithFixedDelay(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    LOG.info("Polling reference queue");
//                    Reference<? extends ReadWriteEpicsChannelImpl<?>> ref = refQueue.remove(0);
//                    if (ref != null) {
//                        LOG.info("reference not null:" + ref.getClass());
//                        if (ch2Ref.get(ref) != null) {
//                            LOG.info("channel not null");
//                            CAJChannel ch = ch2Ref.get(ref);
//                            LOG.info("Destroying channel: " + ch.getName());
//                            ch2Ref.remove(ref);
//                            ch.destroy();
//                        }
//                    }
//                } catch (CAException e) {
//                    LOG.log(Level.SEVERE, e.getMessage(), e);
//                } catch (InterruptedException e) {
//                    LOG.log(Level.SEVERE, e.getMessage(), e);
//                }
//            }
//        }, 1, 1, TimeUnit.MILLISECONDS);
    }

    private void addJCAContextListeners() throws CAException {
        synchronized (_ctx) {
            _ctx.addContextExceptionListener(new ContextExceptionListener() {
                public void contextException(ContextExceptionEvent cee) {
                    LOG.log(Level.WARNING, "Trouble in JCA Context." + cee.getMessage() + " on channel " + cee.getChannel());
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

    protected ReadWriteEpicsChannelImpl<Double> _getDoubleChannel(String channelName) {
        ReadWriteEpicsChannelImpl<Double> ch;

        CAJChannel cajChannel = bindChannel(channelName);
        if (!cajChannel.getFieldType().isDOUBLE()) {
            try {
                cajChannel.destroy();
            } catch (CAException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
            throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
        } else {
            ch = new ReadWriteEpicsChannelImpl<Double>(cajChannel);
//            ch2Ref.put(new PhantomReference<ReadWriteEpicsChannelImpl<?>>(ch, refQueue), cajChannel);
        }

        return ch;
    }

    protected ReadWriteEpicsChannelImpl<Integer> _getIntegerChannel(String channelName) {
        ReadWriteEpicsChannelImpl<Integer> ch;

        CAJChannel cajChannel = bindChannel(channelName);
        if (!cajChannel.getFieldType().isINT()) {
            try {
                cajChannel.destroy();
            } catch (CAException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
            throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
        } else {
            ch = new ReadWriteEpicsChannelImpl<Integer>(cajChannel);
//            ch2Ref.put(new PhantomReference<ReadWriteEpicsChannelImpl<?>>(ch, refQueue), cajChannel);
        }

        return ch;
    }

    protected ReadWriteEpicsChannelImpl<Float> _getFloatChannel(String channelName) {
        ReadWriteEpicsChannelImpl<Float> ch;

        CAJChannel cajChannel = bindChannel(channelName);
        if (!cajChannel.getFieldType().isFLOAT()) {
            try {
                cajChannel.destroy();
            } catch (CAException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
            throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
        } else {
            ch = new ReadWriteEpicsChannelImpl<Float>(cajChannel);
//            ch2Ref.put(new PhantomReference<ReadWriteEpicsChannelImpl<?>>(ch, refQueue), cajChannel);
        }

        return ch;
    }

    protected ReadWriteEpicsChannelImpl<String> _getStringChannel(String channelName) {
        ReadWriteEpicsChannelImpl<String> ch;

        CAJChannel cajChannel = bindChannel(channelName);
        if (!cajChannel.getFieldType().isSTRING()) {
            try {
                cajChannel.destroy();
            } catch (CAException e) {
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
            throw new IllegalArgumentException("Channel " + channelName + " can be connected to, but is of incorrect type.");
        } else {
            ch = new ReadWriteEpicsChannelImpl<String>(cajChannel);
//            ch2Ref.put(new PhantomReference<ReadWriteEpicsChannelImpl<?>>(ch, refQueue), cajChannel);
        }

        return ch;
    }

    protected ReadWriteEpicsChannelImpl<?> _getChannelAsync(String channelName) {
        CAJChannel cajChannel = bindChannelAsync(channelName, null);
        ReadWriteEpicsChannelImpl<?> ch = new ReadWriteEpicsChannelImpl(cajChannel);
//        ch2Ref.put(new PhantomReference<ReadWriteEpicsChannelImpl<?>>(ch, refQueue), cajChannel);
        return ch;

    }

    protected void _destroyChannel(ReadOnlyClientEpicsChannel<?> channel) throws CAException {
        channel.destroy();
    }

    private CAJChannel bindChannelAsync(String channel, ConnectionListener listener) throws EpicsException {
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

    private CAJChannel bindChannel(String channel) throws EpicsException {
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
