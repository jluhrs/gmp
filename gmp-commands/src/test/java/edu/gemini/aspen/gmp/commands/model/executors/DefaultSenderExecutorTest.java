package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.test.ActionSenderMock;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;

/**
 * The default sender executor is trivial. Just use the sender
 * to send the action and returns the answer
 */
public class DefaultSenderExecutorTest {

    private DefaultSenderExecutor _executor;
    private ActionSenderMock _sender;

    private HandlerResponse[] _responses;

    @Before
    public void setUp() {
        _executor = new DefaultSenderExecutor(new JmsActionMessageBuilder());
        _sender = new ActionSenderMock();

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

        Action action = new Action(SequenceCommand.DATUM,
                Activity.START, emptyConfiguration(), null);

        for (HandlerResponse response: _responses) {
            _sender.defineAnswer(response);
            HandlerResponse exResponse =
                    _executor.execute(action, _sender);
            assertEquals(response, exResponse);
        }
    }

}
