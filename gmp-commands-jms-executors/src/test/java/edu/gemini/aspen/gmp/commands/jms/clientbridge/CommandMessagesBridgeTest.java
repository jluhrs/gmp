package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.commands.jms.MockedJmsArtifactsTestBase;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandMessagesBridgeTest extends MockedJmsArtifactsTestBase {

    @Test
    public void testOnMessageWithImmediateResponse() throws JMSException {
        super.createMockedObjects();

        CommandSender commandsSender = mock(CommandSender.class);
        CommandMessagesBridge messagesBridge = new CommandMessagesBridge(provider, commandsSender);

        ArgumentCaptor<Command> commandCaptor = ArgumentCaptor.forClass(Command.class);
        ArgumentCaptor<CompletionListener> listenerCaptor = ArgumentCaptor.forClass(CompletionListener.class);

        HandlerResponse response = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        when(commandsSender.sendCommand(commandCaptor.capture(),listenerCaptor.capture())).thenReturn(response);

        MapMessage message = createApplyCommandMessage();
        messagesBridge.onMessage(message);

        verify(commandsSender).sendCommand(commandCaptor.getValue(), listenerCaptor.getValue());
    }

    private MapMessage createApplyCommandMessage() throws JMSException {
        MapMessage message = mock(MapMessage.class);
        when(message.getStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY)).thenReturn(SequenceCommand.APPLY.toString());
        when(message.getStringProperty(JmsKeys.GMP_ACTIVITY_KEY)).thenReturn(Activity.PRESET.toString());
        CommandMessageParserTest.addConfigurationEntriesToMsg(message);
        return message;
    }

    @Test
    public void testComponentLifeCycle() throws JMSException {
        super.createMockedObjects();

        CommandSender commandsSender = mock(CommandSender.class);
        CommandMessagesBridge messagesBridge = new CommandMessagesBridge(provider, commandsSender);

        messagesBridge.startListeningForMessages();
        verify(session).createConsumer(Mockito.<Destination>any());

        messagesBridge.stopListeningForMessages();
        verify(session).close();
    }
}
