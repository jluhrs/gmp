package edu.gemini.aspen.gmp.commands.test;

import edu.gemini.aspen.giapi.commands.*;


/**
 * A completion listener that can record the last response received and
 * whether it was called or not.
 */
public final class CompletionListenerMock implements CompletionListener {

    private boolean wasInvoked = false;

    private HandlerResponse lastResponse = null;
    
    public CompletionListenerMock() {
    }

    public void onHandlerResponse(HandlerResponse response,
                                  SequenceCommand command,
                                  Activity activity,
                                  Configuration config) {
        synchronized (this) {
            wasInvoked = true;
            lastResponse = response;
            notifyAll();
        }
    }

    public boolean wasInvoked() {
        return wasInvoked;
    }

    public void reset() {
        wasInvoked = false;
        lastResponse = null;
    }

    public HandlerResponse getLastResponse() {
        return lastResponse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompletionListenerMock that = (CompletionListenerMock) o;

        if (wasInvoked != that.wasInvoked) return false;
        if (lastResponse != null ? !lastResponse.equals(that.lastResponse) : that.lastResponse != null)
            return false;
        //equals
        return true;
    }

    @Override
    public int hashCode() {
        int result = (wasInvoked ? 1 : 0);
        result = 31 * result + (lastResponse != null ? lastResponse.hashCode() : 0);
        return result;
    }
}
