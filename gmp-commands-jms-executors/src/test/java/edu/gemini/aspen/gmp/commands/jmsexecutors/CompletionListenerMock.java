package edu.gemini.aspen.gmp.commands.jmsexecutors;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import junit.framework.AssertionFailedError;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * A completion listener that can record the last response received and
 * whether it was called or not.
 */
public final class CompletionListenerMock implements CompletionListener {

    private CountDownLatch invocationLatch = new CountDownLatch(1);

    private HandlerResponse lastResponse = null;
    
    @Override
    public void onHandlerResponse(HandlerResponse response, Command command) {
            lastResponse = response;
            invocationLatch.countDown();
    }

    @Override
    @Deprecated
    public void onHandlerResponse(HandlerResponse response,
                                  SequenceCommand command,
                                  Activity activity,
                                  Configuration config) {
        onHandlerResponse(response, new Command(command, activity, config));
    }

    public void waitForCompletion(long timeout) {
        try {
            invocationLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Convert to runtime exception so tests will fail
            throw new AssertionFailedError("Interrupted wait for invocation");
        }
    }

    public boolean wasInvoked() {
        return invocationLatch.getCount() == 0;
    }

    public void reset() {
        invocationLatch = new CountDownLatch(1);
        lastResponse = null;
    }

    public HandlerResponse getLastResponse() {
        return lastResponse;
    }

    @Override
    public String toString() {
        return "CompletionListenerMock";
    }
}
