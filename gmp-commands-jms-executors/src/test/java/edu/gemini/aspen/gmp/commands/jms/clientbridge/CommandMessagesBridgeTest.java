package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.gmp.commands.jms.MockedJmsArtifactsTestBase;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;
import org.mockito.Mockito;

import javax.jms.Destination;
import javax.jms.JMSException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CommandMessagesBridgeTest extends MockedJmsArtifactsTestBase {

    @Test
    public void testConstruction() {
        JmsProvider jmsProvider = mock(JmsProvider.class);
        CommandSender commandsSender = mock(CommandSender.class);
        assertNotNull(new CommandMessagesBridge(jmsProvider, commandsSender));
    }

    @Test
    public void testComponentLifeCycle() throws JMSException {
        super.createMockedObjects();

        CommandSender commandsSender = mock(CommandSender.class);
        CommandMessagesBridge messagesBridge = new CommandMessagesBridge(provider, commandsSender);

        messagesBridge.startListeningForMessages();

        verify(session).createConsumer(Mockito.<Destination>any());
    }
}
