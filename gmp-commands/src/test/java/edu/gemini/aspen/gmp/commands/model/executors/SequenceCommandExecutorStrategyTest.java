package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandException;
import edu.gemini.aspen.gmp.commands.model.impl.ActionManager;
import edu.gemini.aspen.gmp.commands.test.ActionSenderMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SequenceCommandExecutorStrategyTest {

    private ActionMessageBuilder builder = mock(ActionMessageBuilder.class);
    private ActionManager manager = mock(ActionManager.class);
    private SequenceCommandExecutorStrategy strategy;
    private CompletionListenerMock listener = new CompletionListenerMock();

    @Before
    public void setUp() throws Exception {
        strategy = new SequenceCommandExecutorStrategy(builder, manager, "noscript");
    }


    @Test
    public void testExecuteDefault() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(new Command(SequenceCommand.TEST, Activity.PRESET_START, configuration), listener);
        HandlerResponse response = strategy.execute(action, new ActionSenderMock(HandlerResponse.COMPLETED) {
        });
        assertEquals(HandlerResponse.COMPLETED, response);
    }

    @Test
    public void testExecuteApply() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(new Command(SequenceCommand.APPLY, Activity.PRESET_START, configuration), listener);
        HandlerResponse response = strategy.execute(action, new ActionSenderMock(HandlerResponse.COMPLETED) {
        });
        assertEquals(HandlerResponse.createError(ApplySenderExecutor.ERROR_MSG), response);
    }

    //can't run this test, as it tries to reboot the machine
    @Ignore
    @Test
    public void testExecuteReboot() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(new Command(SequenceCommand.REBOOT, Activity.PRESET_START, configuration), listener, CommandSender.DEFAULT_COMMAND_RESPONSE_TIMEOUT);
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
