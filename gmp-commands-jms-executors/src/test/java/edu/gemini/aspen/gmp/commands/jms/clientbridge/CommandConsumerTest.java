package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class CommandConsumerTest {

    @Test
    public void testConstruction() {
        JmsProvider jmsProvider = mock(JmsProvider.class);
        CommandSender commandsSender = mock(CommandSender.class);
        assertNotNull(new CommandConsumer(jmsProvider, commandsSender));
    }
}
