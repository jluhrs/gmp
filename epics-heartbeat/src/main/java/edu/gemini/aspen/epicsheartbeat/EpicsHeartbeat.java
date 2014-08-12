package edu.gemini.aspen.epicsheartbeat;

import edu.gemini.gmp.top.Top;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatConsumer;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsHeartbeat, receives heartbeats and publishes them to an EPICS channel.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/14/11
 */
public class EpicsHeartbeat implements HeartbeatConsumer {
    private static final Logger LOG = Logger.getLogger(EpicsHeartbeat.class.getName());

    private final ChannelAccessServer cas;

    private Top epicsTop;

    private final String channelName;

    private Channel<Integer> ch = null;

    public EpicsHeartbeat(ChannelAccessServer cas, Top epicsTop, String channelName) {
        this.channelName = channelName;
        this.cas = cas;
        this.epicsTop = epicsTop;
    }

    public void initialize() throws CAException {
        ch = cas.createChannel(epicsTop.buildEpicsChannelName(channelName), -1);
    }

    public void shutdown() {
        try {
            cas.destroyChannel(ch);
        } catch (Exception e) {
            LOG.warning("Exception while shutting down Epics Heartbeat " + e.getMessage());
        }
    }

    public void update() throws CAException {
        shutdown();
        initialize();
    }

    /**
     * Publishes the received beatNumber to the EPICS channel
     *
     * @param beatNumber the last heartbeat number received
     */
    @Override
    public void beat(int beatNumber) {
        try {
            ch.setValue(beatNumber);
        } catch (CAException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (TimeoutException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
