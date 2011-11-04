package edu.gemini.aspen.epicsheartbeat;

import edu.gemini.aspen.gmp.epics.top.EpicsTop;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatConsumer;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsHeartbeat, receives heartbeats and publishes them to an EPICS channel.
 *
 * @author Nicolas A. Barriga
 *         Date: 3/14/11
 */
@Component
@Provides
public class EpicsHeartbeat implements HeartbeatConsumer {
    private static final Logger LOG = Logger.getLogger(EpicsHeartbeat.class.getName());

    @Requires
    private ChannelAccessServer cas;

    @Requires
    private EpicsTop epicsTop;

    @Property(name = "channelName", value = "INVALID", mandatory = true)
    private String channelName;

    private Channel<Integer> ch = null;

    private EpicsHeartbeat() {

    }

    public EpicsHeartbeat(ChannelAccessServer cas, EpicsTop epicsTop, String channelName) {
        this.channelName = channelName;
        this.cas = cas;
        this.epicsTop = epicsTop;
    }

    @Validate
    public void initialize() throws CAException {
        ch = cas.createChannel(epicsTop.buildChannelName(channelName), -1);
    }

    @Invalidate
    public void shutdown() {
        cas.destroyChannel(ch);
    }

    @Updated
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
        }
    }
}
