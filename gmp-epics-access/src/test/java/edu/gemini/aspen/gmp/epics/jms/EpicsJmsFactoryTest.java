package edu.gemini.aspen.gmp.epics.jms;

import com.google.common.collect.ImmutableList;
import edu.gemini.aspen.gmp.epics.EpicsUpdate;
import edu.gemini.aspen.gmp.epics.EpicsUpdateImpl;
import org.junit.Test;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class EpicsJmsFactoryTest {
    private String channelName = "X.val1";
    private Session session = mock(Session.class);
    private BytesMessage bm = mock(BytesMessage.class);

    @Test
    public void createMessageWithIntArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        List<Integer> channelData = ImmutableList.of(4, 5);
        createMessage(channelData);

        // TODO The fact that we need to verify low level protocol here indicates a wrong level of abstraction
        // for EpicsJMSFactory
        verify(bm).writeByte((byte) 2);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.size());
        verify(bm).writeInt(channelData.get(0));
        verify(bm).writeInt(channelData.get(1));
    }

    private void createMessage(List<?> channelData) throws JMSException {
        EpicsUpdate<?> epicsUpdate = new EpicsUpdateImpl(channelName, channelData);
        Message message = EpicsJmsFactory.createMessage(session, epicsUpdate);
        assertNotNull(message);
    }

    @Test
    public void createMessageWithDoubleArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        List<Double> channelData = ImmutableList.of(4.0, 5.0);
        createMessage(channelData);

        verify(bm).writeByte((byte) 3);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.size());
        verify(bm).writeDouble(channelData.get(0));
        verify(bm).writeDouble(channelData.get(1));
    }

    @Test
    public void createMessageWithFloatArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        List<Float> channelData = ImmutableList.of(4.0f, 5.0f);
        createMessage(channelData);

        verify(bm).writeByte((byte) 4);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.size());
        verify(bm).writeFloat(channelData.get(0));
        verify(bm).writeFloat(channelData.get(1));
    }

    @Test
    public void createMessageWithShortArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        List<Short> channelData = ImmutableList.of((short) 4, (short) 5);
        createMessage(channelData);

        verify(bm).writeByte((byte) 1);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.size());
        verify(bm).writeShort(channelData.get(0));
        verify(bm).writeShort(channelData.get(1));
    }

    @Test
    public void createMessageWithByteArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        List<Byte> channelData = ImmutableList.of((byte) 4, (byte) 5);
        createMessage(channelData);

        verify(bm).writeByte((byte) 6);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.size());
        verify(bm).writeByte(channelData.get(0));
        verify(bm).writeByte(channelData.get(1));
    }

    @Test
    public void createMessageWithStringArray() throws JMSException {
        when(session.createBytesMessage()).thenReturn(bm);
        List<String> channelData = ImmutableList.of("a", "b");
        createMessage(channelData);

        verify(bm).writeByte((byte) 5);
        verify(bm).writeUTF(channelName);
        verify(bm).writeInt(channelData.size());
        verify(bm).writeUTF(channelData.get(0));
        verify(bm).writeUTF(channelData.get(1));
    }
}
