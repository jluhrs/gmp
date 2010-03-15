package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.*;
import edu.gemini.aspen.gmp.commands.test.TestActionSender;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.TreeMap;


/**
 *  Test class for the sender of APPLY sequence commands.
 */
public class ApplySenderExecutorTest {

    private ApplySenderExecutor _executor;

    private TestActionSender _sender;

    private HandlerResponse[] _responses;

    private Configuration _applyConfig;

    @Before
    public void setUp() {
        ActionManager actionManager = new ActionManager();
        actionManager.start();
        _executor = new ApplySenderExecutor(new JmsActionMessageBuilder(), actionManager);
        _sender = new TestActionSender();
        _responses = new HandlerResponse[] {
                HandlerResponse.COMPLETED,
                HandlerResponse.STARTED,
                HandlerResponse.ACCEPTED,
                HandlerResponse.NOANSWER,
                HandlerResponse.createError("Error message")
        };


        TreeMap<ConfigPath, String> configuration = new TreeMap<ConfigPath, String>();

        configuration.put(new ConfigPath("X:A.val1"), "xa1");
        configuration.put(new ConfigPath("X:A.val2"), "xa2");
        configuration.put(new ConfigPath("X:A.val3"), "xa3");

        configuration.put(new ConfigPath("X:B.val1"), "xb1");
        configuration.put(new ConfigPath("X:B.val2"), "xb2");
        configuration.put(new ConfigPath("X:B.val3"), "xb3");

        configuration.put(new ConfigPath("X:C.val1"), "xc1");
        configuration.put(new ConfigPath("X:C.val2"), "xc2");
        configuration.put(new ConfigPath("X:C.val3"), "xc3");

        _applyConfig = new DefaultConfiguration(configuration);

    }

    @Test
    public void testNoConfiguration() {
        Action action = new Action(SequenceCommand.APPLY,
                Activity.START,
                null, null);
        HandlerResponse response = _executor.execute(action, _sender);
        assertEquals(HandlerResponse.createError("No configuration present for Apply Sequence command"), response);
    }

    @Test
    public void testEmptyConfiguration() {
        Action action = new Action(SequenceCommand.APPLY,
                Activity.START,
                new DefaultConfiguration(), null);
        HandlerResponse response = _executor.execute(action, _sender);
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
            _sender.defineAnswer(response);
            HandlerResponse myResponse = _executor.execute(action, _sender);
            assertEquals(response, myResponse);
        }
    }









}
