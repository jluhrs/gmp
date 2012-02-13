package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.aspen.gmp.commands.jms.MockedJmsArtifactsTestBase;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import org.junit.Test;

import javax.jms.JMSException;
import java.util.concurrent.TimeUnit;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ActionMessageActionSenderTest extends MockedJmsArtifactsTestBase {

    @Test
    public void testSend() throws JMSException {
        super.createMockedObjects();

        ActionMessageActionSender actionSender = new ActionMessageActionSender();
        actionSender.startJms(provider);

        ActionMessage actionMessage = createActionToSend();
        HandlerResponse response = actionSender.send(actionMessage);

        assertEquals(HandlerResponse.Response.NOANSWER, response.getResponse());

        actionSender.stopJms();
    }

    private ActionMessage createActionToSend() {
        JmsActionMessageBuilder messageBuilder = new JmsActionMessageBuilder();
        Action action = new Action(new Command(SequenceCommand.DATUM,
                Activity.START, emptyConfiguration()), new CompletionListenerMock());

        return messageBuilder.buildActionMessage(action);
    }

    @Test(expected = SequenceCommandException.class)
    public void testErrorWhileSending() throws JMSException, InterruptedException {
        super.createMockedObjects();
        when(session.createMapMessage()).thenThrow(new JMSException(""));

        ActionMessageActionSender actionSender = new ActionMessageActionSender();
        actionSender.startJms(provider);

        TimeUnit.MILLISECONDS.sleep(500);

        ActionMessage actionMessage = createActionToSend();
        actionSender.send(actionMessage);

        actionSender.stopJms();
    }
}
