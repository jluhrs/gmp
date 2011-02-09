package edu.gemini.aspen.gmp.pcs.jms;

import edu.gemini.aspen.gmp.pcs.test.TestPcsUpdater;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Before;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

/**
 * Unit test case for the PcsUpdateListener class
 */
public class PcsUpdateListenerTest {
    private PcsUpdateListener _listener;

    private final TestPcsUpdater _updater = new TestPcsUpdater();

    @Before
    public void setUp() {
        _updater.reset();
        _listener = new PcsUpdateListener(_updater);
    }

    @Test
    public void testValidMessage() throws JMSException {
        Double updates[] = new Double[]{
                1.0,
                2.0,
                3.0,
                4.0,
                5.0
        };

        ActiveMQBytesMessage m = new ActiveMQBytesMessage();
        m.writeInt(updates.length);
        for (Double d : updates) {
            m.writeDouble(d);
        }
        m.reset();

        synchronized (_updater) {
            _listener.onMessage(m);
            try {
                _updater.wait(1);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }

        assertArrayEquals(updates, _updater.getUpdate().getZernikes());
    }

    @Test(expected = JmsPcsMessageException.class)
    public void testWrongContentMessage() throws JMSException {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();
        m.writeInt(-3);
        m.reset();
        _listener.onMessage(m);
    }

    @Test(expected = JmsPcsMessageException.class)
    public void testInvalidMessage() {
        Message m = new ActiveMQMessage();
        _listener.onMessage(m);
    }

    @Test(expected = JmsPcsMessageException.class)
    public void testNotEnoughElements() throws JMSException {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();

        Double updates[] = new Double[]{
                1.0,
                2.0,
                3.0,
                4.0,
                5.0
        };

        m.writeInt(updates.length + 1);
        for (Double d : updates) {
            m.writeDouble(d);
        }
        m.reset();

        _listener.onMessage(m);
    }

    @Test(expected = JmsPcsMessageException.class)
    public void testNotCorrectType() throws JMSException {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();

        Integer updates[] = new Integer[]{
                1,
                2,
                3,
                4,
                5
        };

        m.writeInt(updates.length + 1);
        for (Integer i : updates) {
            m.writeDouble(i);
        }
        m.reset();

        _listener.onMessage(m);
    }

}
