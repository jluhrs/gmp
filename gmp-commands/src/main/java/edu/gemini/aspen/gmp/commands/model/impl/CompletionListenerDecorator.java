package edu.gemini.aspen.gmp.commands.model.impl;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;

/**
 * A decorator for the Completion Listener that will be used. This
 * provides a mechanism to recover the response provided
 * for and action, in case that answer arrives _before_ we
 * provide an answer to the system
 */
class CompletionListenerDecorator implements CompletionListener {
    private final CompletionListener _listener;
    private HandlerResponse _response;

    public CompletionListenerDecorator(CompletionListener listener) {
        Preconditions.checkArgument(listener != null, "Completion Listener cannot be null");
        _listener = listener;
    }

    HandlerResponse getResponse() {
        return _response;
    }

    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
        _listener.onHandlerResponse(response, command);
        //register the response received.
        _response = response;
    }

    @Override
    public String toString() {
        return _listener.toString();
    }
}
