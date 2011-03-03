package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.*;
import edu.gemini.aspen.gmp.commands.test.TestActionSender;
import edu.gemini.aspen.gmp.commands.test.TestCompletionListener;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configurationBuilder;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class SequenceCommandExecutorStrategyTest {

    private ActionMessageBuilder builder = new JmsActionMessageBuilder();
    private ActionSender sender = new TestActionSender();
    private ActionManager manager = mock(ActionManager.class);
    private SequenceCommandExecutorStrategy strategy;
    private TestCompletionListener listener = new TestCompletionListener();

    @Before
    public void setUp() throws Exception {
        strategy = new SequenceCommandExecutorStrategy(builder, manager);
    }

    @Test
    public void testExecuteApply() {
        Configuration configuration = configurationBuilder()
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
        Action action = new Action(SequenceCommand.APPLY, Activity.START, configuration, listener);

        HandlerResponse response = strategy.execute(action, sender);
        assertEquals(HandlerResponse.Response.COMPLETED, response.getResponse());
        assertFalse(listener.wasInvoked());
    }

    @Test
    public void testExecuteApplyWithEmptyConfiguration() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(SequenceCommand.APPLY, Activity.START, configuration, listener);

        HandlerResponse response = strategy.execute(action, sender);
        assertEquals(HandlerResponse.Response.ERROR, response.getResponse());
        assertFalse(listener.wasInvoked());
    }

    @Test
    public void testExecuteRebootWithPreset() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(SequenceCommand.REBOOT, Activity.PRESET, configuration, listener);

        HandlerResponse response = strategy.execute(action, sender);
        assertEquals(HandlerResponse.Response.ACCEPTED, response.getResponse());
        assertFalse(listener.wasInvoked());
    }

    @Test
    public void testExecuteDefault() {
        Configuration configuration = emptyConfiguration();
        Action action = new Action(SequenceCommand.OBSERVE, Activity.PRESET, configuration, listener);

        HandlerResponse response = strategy.execute(action, sender);
        assertNull(response);
    }

    @Test(expected = SequenceCommandException.class)
    public void testExecuteNullAction() {
        Action action = null;
        strategy.execute(action, sender);
    }
}
