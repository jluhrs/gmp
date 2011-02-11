package edu.gemini.aspen.gmp.epics.jms;

import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import org.junit.Test;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class EpicsJmsFactoryTest {
    private String channelName = "X.val1";
    private Session session = mock(Session.class);
    private BytesMessage bm = mock(BytesMessage.class);

    @Test
    public void createMessageWithIntArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        int[] channelData = {4, 5};
        EpicsUpdate epicsUpdate = new EpicsUpdateImpl(channelName, channelData);
        Message message = EpicsJmsFactory.createMessage(session, epicsUpdate);
        assertNotNull(message);
        // TODO The fact that we need to verify low level protocol here indicates a wrong level of abstraction
        // for EpicsJMSFactory
        verify(bm).writeByte((byte)2);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.length);
        verify(bm).writeInt(channelData[0]);
        verify(bm).writeInt(channelData[1]);
    }

    @Test
    public void createMessageWithDoubleArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        double[] channelData = {4, 5};
        EpicsUpdate epicsUpdate = new EpicsUpdateImpl(channelName, channelData);
        Message message = EpicsJmsFactory.createMessage(session, epicsUpdate);
        assertNotNull(message);
        // TODO The fact that we need to verify low level protocol here indicates a wrong level of abstraction
        // for EpicsJMSFactory
        verify(bm).writeByte((byte)3);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.length);
        verify(bm).writeDouble(channelData[0]);
        verify(bm).writeDouble(channelData[1]);
    }
}
