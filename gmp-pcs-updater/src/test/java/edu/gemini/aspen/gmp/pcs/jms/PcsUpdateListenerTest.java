package edu.gemini.aspen.gmp.pcs.jms;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQBytesMessage;

import javax.jms.Message;
import javax.jms.JMSException;

import edu.gemini.aspen.gmp.pcs.test.TestPcsUpdater;

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
    public void testValidMessage() {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();

        Double updates[] = new Double[] {
                1.0,
                2.0,
                3.0,
                4.0,
                5.0
        };

        try {
            m.writeInt(updates.length);
            for (Double d: updates) {
                m.writeDouble(d);
            }
            m.reset();
        } catch (JMSException e) {
            fail("Unexpected problem writting data to JMS Message");
        }

        synchronized (_updater) {
            _listener.onMessage(m);
            try {
                _updater.wait(1);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }

        assertArrayEquals(updates, _updater.getUpdate().getZernikes() );


    }

    @Test
    (expected = JmsPcsMessageException.class)
    public void testWrongContentMessage() {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();
        try {
            m.writeInt(-3);
            m.reset();
        } catch (JMSException e) {
            fail("Unexpected problem writting data to JMS Message");
        }
        _listener.onMessage(m);
    }

    @Test
    (expected = JmsPcsMessageException.class)
    public void testInvalidMessage() {
        Message m = new ActiveMQMessage();
        _listener.onMessage(m);
    }


    @Test
    (expected = JmsPcsMessageException.class)
    public void testNotEnoughElements() {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();

        Double updates[] = new Double[] {
                1.0,
                2.0,
                3.0,
                4.0,
                5.0
        };

        try {
            m.writeInt(updates.length + 1);
            for (Double d: updates) {
                m.writeDouble(d);
            }
            m.reset();
        } catch (JMSException e) {
            fail("Unexpected problem writting data to JMS Message");
        }

        _listener.onMessage(m);

    }

    @Test
    (expected = JmsPcsMessageException.class)
    public void testNotCorrectType() {
        ActiveMQBytesMessage m = new ActiveMQBytesMessage();

        Integer updates[] = new Integer[] {
                1,
                2,
                3,
                4,
                5
        };

        try {
            m.writeInt(updates.length + 1);
            for (Integer i: updates) {
                m.writeDouble(i);
            }
            m.reset();
        } catch (JMSException e) {
            fail("Unexpected problem writting data to JMS Message");
        }

        _listener.onMessage(m);

    }

}
