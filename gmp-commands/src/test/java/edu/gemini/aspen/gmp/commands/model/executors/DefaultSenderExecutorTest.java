package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.test.ActionSenderMock;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * The default sender executor is trivial. Just use the sender
 * to send the action and returns the answer
 */
public class DefaultSenderExecutorTest {

    private DefaultSenderExecutor _executor;

    private HandlerResponse[] _responses;

    @Before
    public void setUp() {
        ActionMessageBuilder builder = mock(ActionMessageBuilder.class);
        _executor = new DefaultSenderExecutor(builder);

        _responses = new HandlerResponse[] {
                HandlerResponse.COMPLETED,
                HandlerResponse.STARTED,
                HandlerResponse.ACCEPTED,
                HandlerResponse.NOANSWER,
                HandlerResponse.createError("Error message")
        };

    }

    /**
     * Test the default executor. This executor will just send the message
     * using the sender, and will return the answer returned by it.
     */
    @Test
    public void testDefaultExecution() {

        Action action = new Action(new Command(SequenceCommand.DATUM,
                Activity.START, emptyConfiguration()), new CompletionListenerMock());

        for (HandlerResponse response: _responses) {
            ActionSender sender = new ActionSenderMock(response);
            HandlerResponse exResponse =
                    _executor.execute(action, sender);
            assertEquals(response, exResponse);
        }
    }

}
