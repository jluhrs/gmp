package edu.gemini.epics.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import edu.gemini.epics.EpicsBase;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.JCAContextController;
import gov.aps.jca.CAException;
import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.TimeoutException;
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
public class EpicsBaseImpl implements EpicsBase {
    private static final Logger LOG = Logger.getLogger(EpicsBaseImpl.class.getName());

    private final Context _ctx;
    private final Map<String, Channel> _channels = Maps.newTreeMap();

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
            throw new EpicsException("Epics channel in incorrect state " + channel, e);
        }
    }

    private void bindNewChannel(String channelName) throws CAException, TimeoutException {
        Channel epicsChannel = _ctx.createChannel(channelName);
        //TODO: Do we need to bind the channels asynchronously, using the connection listener?
        _channels.put(channelName, epicsChannel);
        _ctx.pendIO(5.0);
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
