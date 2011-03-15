package edu.gemini.aspen.epicsheartbeat;

import edu.gemini.aspen.heartbeatdistributor.IHeartbeatConsumer;
import edu.gemini.cas.IChannel;
import edu.gemini.cas.IChannelAccessServer;
import gov.aps.jca.CAException;
import org.apache.felix.ipojo.annotations.*;

import java.util.Dictionary;
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
public class EpicsHeartbeat implements IHeartbeatConsumer {
    private static final Logger LOG = Logger.getLogger(EpicsHeartbeat.class.getName());

    @Requires
    private IChannelAccessServer cas;

    @Property(name = "channelName", value = "INVALID", mandatory = true)
    private String channelName;

    private IChannel<Integer> ch=null;

    private EpicsHeartbeat(){

    }

    public EpicsHeartbeat(IChannelAccessServer cas, String channelName){
        this.channelName=channelName;
        this.cas=cas;
    }

    @Validate
    public void initialize() throws CAException {
        ch=cas.createChannel(channelName,-1);
    }

    @Invalidate
    public void shutdown(){
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
            LOG.log(Level.SEVERE,e.getMessage(),e);
        }
    }
}
