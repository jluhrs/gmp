package edu.gemini.aspen.gmp.commands.test;

import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.model.executors.SequenceCommandExecutor;

import static org.junit.Assert.fail;

/**
 * A Sequence Command executor that can simulate a fast sequence command
 * handler completing _before_ the execute method does.
 */
public class SequenceCommandExecutorMock implements SequenceCommandExecutor {
    private CommandUpdater _commandUpdater;

    private final CompletionListenerMock _completionListener;
    private boolean _simulateFastHandler;
    private HandlerResponse _response;

    public SequenceCommandExecutorMock(CommandUpdater cu, CompletionListenerMock listener) {
        _commandUpdater = cu;
        _simulateFastHandler = false;
        _response = null;
        _completionListener = listener;
    }

    public HandlerResponse execute(Action action, ActionSender sender) {

        if (_simulateFastHandler) {
            _commandUpdater.updateOcs(action.getId(), _response);

            //let's wait for the completion listener to finish
            _completionListener.waitForCompletion(1000);
            if (!_completionListener.wasInvoked()) {
                fail("Thread interrupted");
            }
        }
        return sender.send(null);
    }

    public void simulateFastHandler(HandlerResponse response) {
        _simulateFastHandler = true;
        _response = response;
    }
}
