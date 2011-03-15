package edu.gemini.aspen.epicsheartbeat;

import edu.gemini.aspen.gmp.heartbeat.Heartbeat;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatDistributor;
import edu.gemini.cas.IChannel;
import edu.gemini.cas.impl.ChannelAccessServer;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Class EpicsHeartbeatTest
 *
 * @author Nicolas A. Barriga
 *         Date: 3/14/11
 */
public class EpicsHeartbeatTest {
    BrokerService broker;
    HeartbeatDistributor hbDist;
    Heartbeat hb;
    ChannelAccessServer cas;

    @Before
    public void setUp() throws Exception {

        broker = new BrokerService();
        broker.addConnector("vm://HeartbeatTestBroker");
        broker.setUseJmx(false);
        broker.start();
        JmsProvider provider = new ActiveMQJmsProvider("vm://HeartbeatTestBroker");

        cas = new ChannelAccessServer();
        cas.start();

        hb = new Heartbeat(provider);
        hb.start();

        hbDist = new HeartbeatDistributor(provider);
        hbDist.start();
    }

    @After
    public void tearDown() throws Exception {
        hbDist.stop();
        hb.stop();
        cas.stop();
        broker.stop();

    }

    @Test
    public void test() throws Exception {
        EpicsHeartbeat ehb = new EpicsHeartbeat(cas,"gmp:heartbeat");
        ehb.initialize();
        IChannel<Integer> ch = cas.createChannel("gmp:heartbeat",0);

        hbDist.bindHeartbeatConsumer(ehb);

        Thread.sleep(1200);

        assertNotSame(0,((int[])ch.getValue().getValue())[0]);

        hbDist.unbindHeartbeatConsumer(ehb);
        ehb.shutdown();
    }
}
