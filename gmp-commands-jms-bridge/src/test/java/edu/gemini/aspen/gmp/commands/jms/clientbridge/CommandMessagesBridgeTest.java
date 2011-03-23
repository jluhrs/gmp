package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import edu.gemini.aspen.gmp.commands.jms.MockedJmsArtifactsTestBase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Topic;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandMessagesBridgeTest extends MockedJmsArtifactsTestBase {
    private Destination replyDestination;
    protected CommandSender commandsSender;
    protected CommandMessagesBridge messagesBridge;
    protected ArgumentCaptor<Command> commandCaptor;
    protected ArgumentCaptor<CompletionListener> listenerCaptor;

    @Before
    public void setUp() throws Exception {
        super.createMockedObjects();

        commandsSender = mock(CommandSender.class);
        messagesBridge = new CommandMessagesBridge(provider, commandsSender);
        commandCaptor = ArgumentCaptor.forClass(Command.class);
        listenerCaptor = ArgumentCaptor.forClass(CompletionListener.class);
    }

    @Test
    public void testOnMessageWithImmediateResponse() throws JMSException {
        MapMessage message = mockApplyAndResponse();
        messagesBridge.onMessage(message);

        verify(commandsSender).sendCommand(commandCaptor.capture(), listenerCaptor.capture());

        verify(producer).send(Matchers.<Topic>anyObject(), Matchers.<MapMessage>anyObject());
    }

    private MapMessage mockApplyAndResponse() throws JMSException {
        HandlerResponse response = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        when(commandsSender.sendCommand(commandCaptor.capture(), listenerCaptor.capture())).thenReturn(response);

        return createApplyCommandMessage();
    }

    @Test
    public void testOnMessageWithImmediateAndSecondResponse() throws JMSException {
        MapMessage message = mockApplyAndResponse();
        messagesBridge.onMessage(message);

        verify(commandsSender).sendCommand(commandCaptor.capture(), listenerCaptor.capture());

        verify(producer).send(Matchers.<Topic>anyObject(), Matchers.<MapMessage>anyObject());

        // Now send the complete
        listenerCaptor.getValue().onHandlerResponse(HandlerResponse.get(HandlerResponse.Response.COMPLETED), commandCaptor.getValue());

        // A new message should have been sent over JMS
        verify(producer, times(2)).send(Matchers.<Topic>anyObject(), Matchers.<MapMessage>anyObject());
    }

    private MapMessage createApplyCommandMessage() throws JMSException {
        MapMessage message = new MapMessageMock();
        message.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, SequenceCommand.APPLY.toString());
        message.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, Activity.PRESET.toString());
        message.setString("x:A", "1");
        message.setString("x:B", "2");

        message.setJMSCorrelationID("123");
        return message;
    }

    @Test
    public void testComponentLifeCycle() throws JMSException {
        messagesBridge.startListeningForMessages();
        verify(session).createConsumer(Mockito.<Destination>any());

        messagesBridge.stopListeningForMessages();
        verify(session).close();
    }
}
