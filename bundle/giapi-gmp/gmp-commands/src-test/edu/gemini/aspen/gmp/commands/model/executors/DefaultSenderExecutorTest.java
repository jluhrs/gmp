package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.commands.test.TestActionSender;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;

/**
 * The default sender executor is trivial. Just use the sender
 * to send the action and returns the answer
 */
public class DefaultSenderExecutorTest {

    private DefaultSenderExecutor _executor;
    private TestActionSender _sender;

    private HandlerResponse[] _responses;

    @Before
    public void setUp() {
        _executor = new DefaultSenderExecutor(new JmsActionMessageBuilder());
        _sender = new TestActionSender();

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
                Activity.START, new DefaultConfiguration(), null);

        for (HandlerResponse response: _responses) {
            _sender.defineAnswer(response);
            HandlerResponse exResponse =
                    _executor.execute(action, _sender);
            assertEquals(response, exResponse);
        }
    }

}
