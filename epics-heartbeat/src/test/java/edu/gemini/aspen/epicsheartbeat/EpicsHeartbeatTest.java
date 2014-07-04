package edu.gemini.aspen.epicsheartbeat;

import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.aspen.gmp.heartbeat.Heartbeat;
import edu.gemini.gmp.top.Top;
import edu.gemini.gmp.top.TopImpl;
import edu.gemini.aspen.heartbeatdistributor.HeartbeatDistributor;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.api.Channel;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.apache.activemq.broker.BrokerService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotSame;


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
    ChannelAccessServerImpl cas;
    Top top;
    StatusSetterImpl ss;

    static {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");
    }

    @Before
    public void setUp() throws Exception {

        broker = new BrokerService();
        broker.addConnector("vm://HeartbeatTestBroker");
        broker.setUseJmx(false);
        broker.setPersistent(false);
        broker.start();
        JmsProvider provider = new ActiveMQJmsProvider("vm://HeartbeatTestBroker");

        cas = new ChannelAccessServerImpl();
        cas.start();

        top = new TopImpl("gpitest","gpitest");
        ss = new StatusSetterImpl("name", "set");
        ss.startJms(provider);
        hb = new Heartbeat("gmp:heartbeat",true,top,ss);
        hb.startJms(provider);

        hbDist = new HeartbeatDistributor();
        hbDist.startJms(provider);

    }

    @After
    public void tearDown() throws Exception {
        hbDist.stopJms();
        ss.stopJms();
        hb.stopJms();
        cas.stop();
        broker.stop();

    }

    @Test
    public void test() throws Exception {
        EpicsHeartbeat ehb = new EpicsHeartbeat(cas, top, "gmp:heartbeat");
        ehb.initialize();
        Channel<Integer> ch = cas.createChannel("gpitest:gmp:heartbeat", 0);

        hbDist.bindHeartbeatConsumer(ehb);

        Thread.sleep(1200);

        assertNotSame(0, ((int[]) ch.getDBR().getValue())[0]);

        hbDist.unbindHeartbeatConsumer(ehb);
        ehb.shutdown();
    }
}
