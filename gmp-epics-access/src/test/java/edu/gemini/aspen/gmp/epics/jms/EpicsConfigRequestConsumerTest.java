package edu.gemini.aspen.gmp.epics.jms;

import com.google.common.collect.ImmutableSet;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.epics.EpicsConfiguration;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;

import javax.jms.*;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EpicsConfigRequestConsumerTest {

    @Test
    public void testConstruction() throws JMSException {
        EpicsConfiguration configuration = new EpicsConfiguration() {
            @Override
            public Set<String> getValidChannelsNames() {
                return ImmutableSet.of("X.val1");
            }
        };

        JmsProvider provider = buildMocks();
        EpicsConfigRequestConsumer consumer = new EpicsConfigRequestConsumer(provider, configuration);
        assertNotNull(consumer);
    }

    private JmsProvider buildMocks() throws JMSException {
        JmsProvider provider = mock(JmsProvider.class);
        ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
        when(provider.getConnectionFactory()).thenReturn(connectionFactory);
        Connection connection = mock(Connection.class);
        when(connectionFactory.createConnection()).thenReturn(connection);
        Session session = mock(Session.class);
        when(connection.createSession(anyBoolean(), anyInt())).thenReturn(session);

        Queue destination = mock(Queue.class);
        when(session.createQueue(anyString())).thenReturn(destination);
        MessageConsumer messageConsumer = mock(MessageConsumer.class);
        when(session.createConsumer(destination)).thenReturn(messageConsumer);
        return provider;
    }

    @Test
    public void onMessageWithoutChannelProperty() throws JMSException {
        EpicsConfiguration configuration = new EpicsConfiguration() {
            @Override
            public Set<String> getValidChannelsNames() {
                return ImmutableSet.of("X.val1");
            }
        };

        JmsProvider provider = buildMocks();
        EpicsConfigRequestConsumer consumer = new EpicsConfigRequestConsumer(provider, configuration);
        Message message = mock(Message.class);

        when(message.getBooleanProperty(JmsKeys.GMP_GEMINI_EPICS_CHANNEL_PROPERTY)).thenReturn(false);
        consumer.onMessage(message);

        verify(message).getBooleanProperty(JmsKeys.GMP_GEMINI_EPICS_CHANNEL_PROPERTY);
        verifyNoMoreInteractions(message);
    }

}
