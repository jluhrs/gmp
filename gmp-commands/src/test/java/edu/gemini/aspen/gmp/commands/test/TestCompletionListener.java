package edu.gemini.aspen.gmp.commands.test;

import edu.gemini.aspen.giapi.commands.*;
import org.junit.Ignore;


/**
 * A completion listener that can record the last response received and
 * whether it was called or not.
 */
@Ignore
public class TestCompletionListener implements CompletionListener {

    private boolean wasInoked = false;

    private HandlerResponse lastResponse = null;
    
    public TestCompletionListener() {
    }

    public void onHandlerResponse(HandlerResponse response,
                                  SequenceCommand command,
                                  Activity activity,
                                  Configuration config) {
        synchronized (this) {
            wasInoked = true;
            lastResponse = response;
            notifyAll();
        }
    }

    public boolean wasInvoked() {
        return wasInoked;
    }

    public void reset() {
        wasInoked = false;
        lastResponse = null;
    }

    public HandlerResponse getLastResponse() {
        return lastResponse;
    }
}
