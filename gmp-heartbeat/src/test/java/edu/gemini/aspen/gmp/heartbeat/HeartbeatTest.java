package edu.gemini.aspen.gmp.heartbeat;

import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.aspen.gmp.heartbeat.jms.JmsHeartbeatConsumer;
import edu.gemini.gmp.top.TopImpl;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.apache.activemq.broker.BrokerService;
import org.junit.Test;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Class HeartbeatTest
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class HeartbeatTest {
    private int last = 0;

    private class HeartbeatListener implements MessageListener {
        private final Logger LOG = Logger.getLogger(HeartbeatListener.class.getName());

        @Override
        public void onMessage(Message message) {
            if (message instanceof BytesMessage) {
                BytesMessage bm = (BytesMessage) message;
                try {
                    last = bm.readInt();
                    LOG.info("Heartbeat: " + last);
                } catch (JMSException ex) {
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            } else {
                LOG.warning("Wrong message type");
            }
        }
    }

    private HeartbeatListener hbl = new HeartbeatListener();

    @Test
    public void testBasic() throws Exception {
        long secs = 3;
        BrokerService broker = new BrokerService();

        broker.addConnector("vm://HeartbeatTestBroker");
        broker.setUseJmx(false);
        broker.start();
        JmsProvider provider = new ActiveMQJmsProvider("vm://HeartbeatTestBroker");
        StatusSetterImpl ss = new StatusSetterImpl("name", "name");
        ss.startJms(provider);
        Heartbeat hb = new Heartbeat("gmp:heartbeat", true, new TopImpl("gpisim", "gpisim"), ss);
        JmsHeartbeatConsumer hbc = new JmsHeartbeatConsumer("Test HeartBeat Consumer", hbl);
        hbc.start(provider);
        hb.startJms(provider);

        Thread.sleep(secs * 1000);
        hb.stopJms();
        hbc.stop();
        broker.stop();
        assertEquals(true, last >= (secs - 1));
    }

    /**
     * Main method that prints to stdout any heartbeats it receives
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        long secs = 3;
        HeartbeatTest test = new HeartbeatTest();

        JmsHeartbeatConsumer hbc = new JmsHeartbeatConsumer("Test HeartBeat Consumer", test.hbl);
        hbc.start(new ActiveMQJmsProvider("tcp://localhost:61616"));

        Thread.sleep(secs * 1000);
        hbc.stop();
        assertEquals(true, test.last >= (secs - 1));
    }
}
