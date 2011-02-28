package edu.gemini.aspen.gmp.heartbeat;


import edu.gemini.aspen.gmp.heartbeat.jms.HeartbeatConsumer;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
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

        Heartbeat hb = new Heartbeat(new ActiveMQJmsProvider("vm://HeartbeatTestBroker"));
        HeartbeatConsumer hbc = new HeartbeatConsumer();
        hbc.start("vm://HeartbeatTestBroker");
        hb.start();

        Thread.sleep(secs * 1000);
        hb.stop();
        hbc.stop();
        broker.stop();
        assertEquals(true, hbc.getLast() >= (secs - 1));
    }

    /**
     * Main method that prints to stdout any heartbeats it receives
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception{
        long secs = 3;

        HeartbeatConsumer hbc = new HeartbeatConsumer();
        hbc.start("tcp://localhost:61616");

        Thread.sleep(secs * 1000);
        hbc.stop();
        assertEquals(true, hbc.getLast() >= (secs - 1));
    }
}
