package edu.gemini.aspen.giapi.data.obsevents;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.data.ObservationEventHandler;
import edu.gemini.aspen.gmp.data.ObservationEvent;
import edu.gemini.aspen.gmp.data.Dataset;

/**
 * Base test class for testing the ObservationEventMonitor interface
 */
public abstract class ObservationEventMonitorTestBase {

    private int flagValue = 0;

    private final ObservationEventHandler handlerStub = new ObservationEventHandler() {
        public void onObservationEvent(ObservationEvent event, Dataset dataset) {
            synchronized (this) {
                ++flagValue;
                notify();
            }
        }
    };

    /**
     * Return the concrete implementation of the Observation Event
     * Monitor to be tested
     * @return an Observation Event Monitor
     */
    public abstract ObservationEventMonitor getMonitor();

    /**
     * This methods triggers the registered handlers to be invoked. 
     * The implementation of this depends on the specific implementation.
     * For instance, in JMS, it will put a new message in the callback
     * that invokes the registered handlers.
     */
    public abstract void triggerHandler();

    @Test
    public void testRegisterHandler() {
        flagValue = 0;
        getMonitor().registerHandler(handlerStub);
        triggerHandler();
        synch();
        assertEquals(1, flagValue);
    }

    /**
     * Auxiliary method to synchronize triggering a handler
     * and the actual execution of it. 
     */
    private void synch() {
        synchronized (handlerStub) {
            try {
                handlerStub.wait(500);
            } catch (InterruptedException e) {
                fail("Interrupted while waiting for thread to be called");
            }
        }
    }

    @Test
    public void testUnregisterHandler() {
        //first, register a handler, make sure it is invoked
        flagValue = 0;
        getMonitor().registerHandler(handlerStub);
        triggerHandler(); synch();
        assertEquals(1, flagValue);

        //deregister it, and make sure we don't
        //see the effect of the handler being invoked
        flagValue = 0;
        getMonitor().unregisterHandler(handlerStub);
        triggerHandler(); synch();
        assertEquals(0, flagValue);

    }

    @Test
    public void testMultipleRegistration() {
        flagValue = 0;
        getMonitor().registerHandler(handlerStub);
        getMonitor().registerHandler(handlerStub);
        getMonitor().registerHandler(handlerStub);
        triggerHandler(); synch();
        assertEquals(1, flagValue);
    }

    @Test
    public void testNullRegistration() {
        flagValue = 0;
        getMonitor().registerHandler(null);
        triggerHandler(); synch();
        assertEquals(0, flagValue);
    }

}
