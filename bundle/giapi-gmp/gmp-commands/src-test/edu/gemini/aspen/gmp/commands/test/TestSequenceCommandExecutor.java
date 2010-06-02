package edu.gemini.aspen.gmp.commands.test;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.gmp.commands.model.SequenceCommandExecutor;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

import static org.junit.Assert.fail;

/**
 * A Sequence Command executor that can simulate a fast sequence command
 * handler completing _before_ the execute method does.
 */
public class TestSequenceCommandExecutor implements SequenceCommandExecutor {

    private CommandUpdater _commandUpdater;

    private final CompletionListener _completionListener;
    private boolean _simulateFastHandler;
    private HandlerResponse _response;

    public TestSequenceCommandExecutor(CommandUpdater cu, CompletionListener listener) {
        _commandUpdater = cu;
        _simulateFastHandler = false;
        _response = null;
        _completionListener = listener;

    }


    public HandlerResponse execute(Action action, ActionSender sender) {

        if (_simulateFastHandler) {
            _commandUpdater.updateOcs(action.getId(), _response);

            //let's wait for the completion listener to finish
            synchronized (_completionListener) {
                try {
                    _completionListener.wait(1000);
                } catch (InterruptedException e) {
                    fail("Thread interrupted");
                }
            }
        }
        return sender.send(null);
    }

    public void simulateFastHandler(HandlerResponse response) {
        _simulateFastHandler = true;
        _response = response;
    }
}
