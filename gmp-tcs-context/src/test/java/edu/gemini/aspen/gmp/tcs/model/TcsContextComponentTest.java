package edu.gemini.aspen.gmp.tcs.model;

import edu.gemini.epics.EpicsException;
import edu.gemini.epics.EpicsReader;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import javax.jms.*;

import static edu.gemini.aspen.gmp.tcs.model.EpicsTcsContextFetcher.TCS_CONTEXT_CHANNEL;
import static org.mockito.Mockito.*;

public class TcsContextComponentTest {

    private JmsProvider provider;
    private Session session;
    private EpicsReader reader;

    @Before
    public void setUp() throws Exception {
        provider = mock(JmsProvider.class);
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(provider.getConnectionFactory()).thenReturn(connectionFactory);
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);
        session = mock(Session.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);
        BytesMessage bytesMessage = mock(BytesMessage.class);
        when(session.createBytesMessage()).thenReturn(bytesMessage);
        MessageProducer producer = mock(MessageProducer.class);
        when(session.createProducer(Matchers.<Destination>anyObject())).thenReturn(producer);

        reader = mock(EpicsReader.class);
    }

    @Test
    public void testValidation() throws JMSException {
        TcsContextComponent component = new TcsContextComponent(provider, reader, TCS_CONTEXT_CHANNEL);
        component.validated();

        verify(provider, times(2)).getConnectionFactory();
        verifyZeroInteractions(reader);
    }

    @Test
    public void testReaderAvailableAndValidation() throws JMSException, EpicsException {
        TcsContextComponent component = new TcsContextComponent(provider, reader, TCS_CONTEXT_CHANNEL);

        component.registerEpicsReader();
        component.validated();

        verify(reader).bindChannel(TCS_CONTEXT_CHANNEL);
    }

    @Test
    public void testReaderModified() throws JMSException, EpicsException {
        TcsContextComponent component = new TcsContextComponent(provider, reader, TCS_CONTEXT_CHANNEL);

        component.registerEpicsReader();
        component.validated();
        component.modifiedEpicsReader();

        verify(reader, times(2)).bindChannel(TCS_CONTEXT_CHANNEL);
    }

    @Test
    public void testReaderAvailableAndRemoved() throws JMSException, EpicsException {
        TcsContextComponent component = new TcsContextComponent(provider, reader, TCS_CONTEXT_CHANNEL);

        component.registerEpicsReader();
        component.validated();
        component.unRegisterEpicsReader();

        //FIXME: This should unbind the epics channel
        verify(reader).bindChannel(TCS_CONTEXT_CHANNEL);
    }
}
