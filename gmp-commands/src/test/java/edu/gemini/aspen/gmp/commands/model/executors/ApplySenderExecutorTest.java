package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.test.ActionSenderMock;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;


/**
 *  Test class for the sender of APPLY sequence commands.
 */
public class ApplySenderExecutorTest {

    private ApplySenderExecutor _executor;

    private HandlerResponse[] _responses;

    private Configuration _applyConfig;

    @Before
    public void setUp() {
        ActionManager actionManager = new ActionManager();
        actionManager.start();
        _executor = new ApplySenderExecutor(new JmsActionMessageBuilder(), actionManager);
        _responses = new HandlerResponse[] {
                HandlerResponse.COMPLETED,
                HandlerResponse.STARTED,
                HandlerResponse.ACCEPTED,
                HandlerResponse.NOANSWER,
                HandlerResponse.createError("Error message")
        };

        _applyConfig = configurationBuilder()
                .withPath(configPath("X:A.val1"), "xa1")
                .withPath(configPath("X:A.val2"), "xa2")
                .withPath(configPath("X:A.val2"), "xa2")
                .withPath(configPath("X:A.val3"), "xa3")
                .withPath(configPath("X:B.val1"), "xb1")
                .withPath(configPath("X:B.val1"), "xb1")
                .withPath(configPath("X:B.val2"), "xb2")
                .withPath(configPath("X:B.val3"), "xb3")
                .withPath(configPath("X:C.val1"), "xc1")
                .withPath(configPath("X:C.val2"), "xc2")
                .withPath(configPath("X:C.val3"), "xc3")
                .build();
    }

    @Test
    public void testNoConfiguration() {
        Action action = new Action(SequenceCommand.APPLY,
                Activity.START,
                null, null);
        ActionSender sender = new ActionSenderMock(HandlerResponse.ACCEPTED);
        HandlerResponse response = _executor.execute(action, sender);
        assertEquals(HandlerResponse.createError("No configuration present for Apply Sequence command"), response);
    }

    @Test
    public void testEmptyConfiguration() {
        Action action = new Action(SequenceCommand.APPLY,
                Activity.START,
                emptyConfiguration(), null);
        ActionSender sender = new ActionSenderMock(HandlerResponse.ACCEPTED);
        HandlerResponse response = _executor.execute(action, sender);
        assertEquals(HandlerResponse.createError("No configuration present for Apply Sequence command"), response);
    }

    /**
     * This test verifies that the handler response produced
     * by the execute() method is the correct one.
     */
    @Test
    public void testCorrectHandlerResponse() {
        Action action = new Action(SequenceCommand.APPLY,
                Activity.START,
                _applyConfig, null);

        for (HandlerResponse response: _responses) {
            ActionSender sender = new ActionSenderMock(response);
            HandlerResponse myResponse = _executor.execute(action, sender);
            assertEquals(response, myResponse);
        }
    }
}
