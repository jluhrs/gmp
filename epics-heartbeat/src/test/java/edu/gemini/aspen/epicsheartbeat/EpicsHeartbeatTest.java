package edu.gemini.aspen.epicsheartbeat;

import edu.gemini.aspen.gmp.heartbeat.Heartbeat;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatDistributor;
import edu.gemini.cas.IChannel;
import edu.gemini.cas.impl.ChannelAccessServer;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.apache.activemq.broker.BrokerService;
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

    @Test
    public void test() throws Exception {

        BrokerService broker = new BrokerService();
        broker.addConnector("vm://HeartbeatTestBroker");
        broker.setUseJmx(false);
        broker.start();
        JmsProvider provider = new ActiveMQJmsProvider("vm://HeartbeatTestBroker");

        ChannelAccessServer cas= new ChannelAccessServer();
        cas.start();

        Heartbeat hb = new Heartbeat(provider);
        hb.start();

        HeartbeatDistributor hbd = new HeartbeatDistributor(provider);
        hbd.start();

        EpicsHeartbeat ehb = new EpicsHeartbeat(cas,"gmp:heartbeat");
        ehb.initialize();
        IChannel<Integer> ch = cas.createChannel("gmp:heartbeat",0);

        hbd.bindHeartbeatConsumer(ehb);

        Thread.sleep(1200);

        assertNotSame(0,((int[])ch.getValue().getValue())[0]);

        hbd.unbindHeartbeatConsumer(ehb);
        ehb.shutdown();
        hbd.stop();
        hb.stop();
        cas.stop();
        broker.stop();
    }
}
