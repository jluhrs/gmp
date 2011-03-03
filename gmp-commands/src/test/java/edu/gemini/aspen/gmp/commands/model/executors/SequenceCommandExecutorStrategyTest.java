package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.*;
import edu.gemini.aspen.gmp.commands.test.ActionSenderMock;
import edu.gemini.aspen.gmp.commands.test.CompletionListenerMock;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SequenceCommandExecutorStrategyTest {

    private ActionMessageBuilder builder = new JmsActionMessageBuilder();
    private ActionManager manager = mock(ActionManager.class);
    private SequenceCommandExecutorStrategy strategy;
    private CompletionListenerMock listener = new CompletionListenerMock();

    @Before
    public void setUp() throws Exception {
        strategy = new SequenceCommandExecutorStrategy(builder, manager);
    }


    @Test
    public void testExecuteDefault() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(SequenceCommand.OBSERVE, Activity.PRESET_START, configuration, listener);
        HandlerResponse response = strategy.execute(action, new ActionSenderMock(HandlerResponse.COMPLETED) {
        });
        assertEquals(HandlerResponse.COMPLETED, response);
    }

    @Test
    public void testExecuteApply() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(SequenceCommand.APPLY, Activity.PRESET_START, configuration, listener);
        HandlerResponse response = strategy.execute(action, new ActionSenderMock(HandlerResponse.COMPLETED) {
        });
        assertEquals(HandlerResponse.createError(ApplySenderExecutor.ERROR_MSG), response);
    }

    @Test
    public void testExecuteReboot() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(SequenceCommand.REBOOT, Activity.PRESET_START, configuration, listener);
        HandlerResponse response = strategy.execute(action, new ActionSenderMock(HandlerResponse.COMPLETED) {
        });
        assertEquals(HandlerResponse.COMPLETED, response);
    }



    @Test(expected = SequenceCommandException.class)
    public void testExecuteNullAction() {
        Action action = null;
        ActionSender sender = new ActionSenderMock(HandlerResponse.COMPLETED);
        strategy.execute(action, sender);
    }
}
