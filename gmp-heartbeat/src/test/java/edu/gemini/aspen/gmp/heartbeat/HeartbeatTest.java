package edu.gemini.aspen.gmp.heartbeat;


import edu.gemini.aspen.gmp.heartbeat.jms.HeartbeatConsumer;
import org.apache.activemq.broker.BrokerService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Class HeartbeatTest
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class HeartbeatTest{
    @Test
    public void testBasic() throws Exception{
        long secs = 3;
        BrokerService broker = new BrokerService();

        broker.addConnector("vm://HeartbeatTestBroker");
        broker.setUseJmx(false);
        broker.start();

        Heartbeat hb = new Heartbeat();
        HeartbeatConsumer hbc = new HeartbeatConsumer();
        hbc.start("vm://HeartbeatTestBroker");
        hb.start("vm://HeartbeatTestBroker");

        Thread.sleep(secs * 1000);
        hb.stop();
        hbc.stop();
        broker.stop();
        assertEquals(true, hbc.getLast() >= (secs - 1));
    }
}
