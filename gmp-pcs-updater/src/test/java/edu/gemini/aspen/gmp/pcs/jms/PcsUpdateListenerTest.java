package edu.gemini.aspen.gmp.pcs.jms;

import edu.gemini.aspen.gmp.pcs.model.PcsUpdate;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdater;
import edu.gemini.aspen.gmp.pcs.model.PcsUpdaterException;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.JMSException;
import javax.jms.Message;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit test case for the PcsUpdateListener class
 */
public class PcsUpdateListenerTest {
    private PcsUpdateListener _listener;
    private final PcsUpdater _updater = mock(PcsUpdater.class);
    private ActiveMQBytesMessage m;

    @Before
    public void setUp() {
        _listener = new PcsUpdateListener(_updater);
        m = new ActiveMQBytesMessage();
    }

    @Test
    public void testValidMessage() throws JMSException, PcsUpdaterException {
        Double updates[] = new Double[]{
                1.0,
                2.0,
                3.0,
                4.0,
                5.0
        };

        m.writeInt(updates.length);
        for (Double d : updates) {
            m.writeDouble(d);
        }
        m.reset();

        _listener.onMessage(m);
        ArgumentCaptor<PcsUpdate> updateCapture = ArgumentCaptor.forClass(PcsUpdate.class);
        verify(_updater).update(updateCapture.capture());

        assertEquals(updateCapture.getValue(), new PcsUpdate(updates));
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
