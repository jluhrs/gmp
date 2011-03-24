package edu.gemini.aspen.gmp.commands.jms.client;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.DefaultConfiguration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.giapi.util.jms.test.MapMessageMock;
import edu.gemini.aspen.giapitestsupport.TesterException;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandSenderClientTest extends MockedJMSArtifactsBase {
    private CommandSenderClient senderClient;
    private CompletionListenerMock completionListener;
    private Executor executor = Executors.newSingleThreadExecutor();

    @Before
    public void setUp() throws JMSException, TesterException {
        createMockedObjects();
        senderClient = new CommandSenderClient(provider);
        completionListener = new CompletionListenerMock();
    }

    @Test
    public void testSendParkCommand() throws TesterException, JMSException {
        buildSimulatedInitialReply(HandlerResponse.Response.COMPLETED);

        Command command = new Command(SequenceCommand.PARK, Activity.START);
        HandlerResponse response = senderClient.sendCommand(command, completionListener, 1000);

        // Initial response
        assertEquals(HandlerResponse.COMPLETED, response);
        // No later command
        assertFalse(completionListener.wasInvoked());
    }

    private void buildSimulatedInitialReply(HandlerResponse.Response replyResponse) throws JMSException {
        HandlerResponseMapMessage completedReply = new HandlerResponseMapMessage(HandlerResponse.get(replyResponse));
        mockReplyMessage(completedReply);
    }

    @Test
    public void testSendApplyWithLateResponse() throws TesterException, JMSException, InterruptedException {
        buildSimulatedInitialReply(HandlerResponse.Response.STARTED);

        final ArgumentCaptor<MessageListener> listenerCaptor = ArgumentCaptor.forClass(MessageListener.class);

        Command command = new Command(SequenceCommand.APPLY, Activity.START, DefaultConfiguration.emptyConfiguration());

        HandlerResponse response = senderClient.sendCommand(command, completionListener);
        assertEquals(HandlerResponse.STARTED, response);

        // Simulate that a reply was sent later on in a separate thread
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    verify(consumer).setMessageListener(listenerCaptor.capture());
                    MapMessage replyMessage = mockCompletionInformationMessage();

                    listenerCaptor.getValue().onMessage(replyMessage);
                } catch (JMSException e) {
                    fail();
                }
            }
        });

        TimeUnit.MILLISECONDS.sleep(200L);

        assertTrue(completionListener.wasInvoked());
    }

    @Test
    public void testSendApplyWithImmediateCompletion() throws TesterException, JMSException {
        buildSimulatedInitialReply(HandlerResponse.Response.COMPLETED);

        Command command = new Command(SequenceCommand.APPLY, Activity.START, DefaultConfiguration.emptyConfiguration());

        HandlerResponse response = senderClient.sendCommand(command, completionListener);
        assertEquals(HandlerResponse.COMPLETED, response);

        assertFalse(completionListener.wasInvoked());
    }

    @Test
    public void testSendApplyWithoutConfiguration() throws TesterException, JMSException {
        buildSimulatedInitialReply(HandlerResponse.Response.ERROR);

        Command command = new Command(SequenceCommand.APPLY, Activity.START, DefaultConfiguration.emptyConfiguration());

        HandlerResponse response = senderClient.sendCommand(command, completionListener);
        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());

        verify(session).close();
    }

    private MapMessage mockCompletionInformationMessage() throws JMSException {
        MapMessageMock replyMessage = new MapMessageMock();
        replyMessage.setStringProperty(JmsKeys.GMP_HANDLER_RESPONSE_KEY, "COMPLETED");
        replyMessage.setStringProperty(JmsKeys.GMP_SEQUENCE_COMMAND_KEY, "APPLY");
        replyMessage.setStringProperty(JmsKeys.GMP_ACTIVITY_KEY, "START");
        return replyMessage;
    }

    @Test
    public void testSendCommandWhenDisconnected() throws TesterException, JMSException {
        when(connectionFactory.createConnection()).thenThrow(new JMSException("Error"));

        Command command = new Command(SequenceCommand.PARK, Activity.START);
        HandlerResponse response = senderClient.sendCommand(command, completionListener);
        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());
    }

    @Test
    public void testSendCommandWithASpuriousException() throws TesterException, JMSException {
        when(connectionFactory.createConnection()).thenThrow(new RuntimeException("Error"));

        Command command = new Command(SequenceCommand.PARK, Activity.START);
        HandlerResponse response = senderClient.sendCommand(command, completionListener);
        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());
    }

}
