package edu.gemini.aspen.giapi.data.obsevents;

import edu.gemini.aspen.giapi.data.DataLabel;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.aspen.giapi.data.ObservationEvent;

import java.util.*;

/**
 * Base test class for testing the ObservationEventHandlerComposite interface
 */
public abstract class ObservationEventHandlerCompositeTestBase {

    private int flagValue = 0;

    Random random = new Random();

    private List<ObservationEvent> events;

    @Before
    public void setUp() {
        flagValue = 0;
        events = Collections.synchronizedList(new ArrayList<ObservationEvent>());
    }

    @After
    public void tearDown() {
        events = null;
    }

    /**
     * An example handler, that increases a class counter so we can verify
     * how many times it has been invoked
     */
    private final ObservationEventHandler singleHandlerStub = new ObservationEventHandler() {
        public void onObservationEvent(ObservationEvent event, DataLabel dataLabel) {
            synchronized (this) {
                ++flagValue;
                notifyAll();
            }
        }
    };

    /**
     * A handler that "takes some time" to process. This is used to test
     * operations involving several threads
     */
    private final ObservationEventHandler waitingHandler = new ObservationEventHandler() {
        public void onObservationEvent(ObservationEvent event, DataLabel dataLabel) {
            events.add(event);
            try {
                //add some random time, to simulate processing.
                Thread.sleep(random.nextInt(20));
            } catch (InterruptedException e) {
                fail("Event handler interrupted");
            }
            //Notify when the last event is received. 
            if (event == ObservationEvent.OBS_END_DSET_WRITE) {
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    };


    /**
     * Return the concrete implementation of the composite to be tested
     *
     * @return the concrete implementation of the composite to be tested
     */
    public abstract ObservationEventHandlerComposite getHandlerComposite();

    /**
     * This methods triggers the registered handlers to be invoked.
     */
    public void triggerHandler() {
        getHandlerComposite().onObservationEvent(ObservationEvent.OBS_PREP,
                new DataLabel("TEST-DATASET"));
    }

    @Test
    public void testRegisterHandler() {
        getHandlerComposite().registerHandler(singleHandlerStub);
        triggerHandler();
        synch();
        assertEquals(1, flagValue);
    }

    /**
     * Auxiliary method to synchronize triggering a handler
     * and the actual execution of it.
     */
    private void synch() {
        synchronized (singleHandlerStub) {
            try {
                singleHandlerStub.wait(10000);
            } catch (InterruptedException e) {
                fail("Interrupted while waiting for thread to be called");
            }
        }
    }

    @Test
    public void testUnregisterHandler() {
        //first, register a handler, make sure it is invoked
        getHandlerComposite().registerHandler(singleHandlerStub);
        triggerHandler();
        synch();
        assertEquals(1, flagValue);

        //deregister it, and make sure it got removed
        flagValue = 0;
        getHandlerComposite().unregisterHandler(singleHandlerStub);
        assertEquals(0, getHandlerComposite().getHandlers().size());

    }

    @Test
    public void testMultipleRegistration() {
        getHandlerComposite().registerHandler(singleHandlerStub);
        getHandlerComposite().registerHandler(singleHandlerStub);
        getHandlerComposite().registerHandler(singleHandlerStub);
        triggerHandler();
        synch();
        assertEquals(1, flagValue);
    }

    @Test
    public void testNullRegistration() {
        getHandlerComposite().registerHandler(null);
        assertEquals(0, getHandlerComposite().getHandlers().size());

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testForbidModifyHandlerSet() {
        Set<ObservationEventHandler> handlers = getHandlerComposite().getHandlers();


        //try to add a fake handler. This is not supported since the
        //returned set should be unmodifiable.
        handlers.add(new ObservationEventHandler() {
            public void onObservationEvent(ObservationEvent event, DataLabel dataLabel) {
                //nothing
            }
        });
    }


    @Test
    public void testEventsReceivedInOrder() {
        List<ObservationEvent> testEvents = Arrays.asList(ObservationEvent.OBS_PREP,
                ObservationEvent.OBS_START_ACQ,
                ObservationEvent.OBS_END_ACQ,
                ObservationEvent.OBS_START_READOUT,
                ObservationEvent.OBS_END_READOUT,
                ObservationEvent.OBS_START_DSET_WRITE,
                ObservationEvent.OBS_END_DSET_WRITE
        );

        for (int i = 0; i < 100; i++) {
            events = Collections.synchronizedList(new ArrayList<ObservationEvent>());
            sendObservationEvents(testEvents);
            assertEquals(testEvents, events);
        }
    }


    public void sendObservationEvents(List<ObservationEvent> testEvents) {

        getHandlerComposite().registerHandler(waitingHandler);

        DataLabel dataLabel = new DataLabel("TEST_DATASET");
        for (ObservationEvent event : testEvents) {
            getHandlerComposite().onObservationEvent(event, dataLabel);
        }

        //wait for the last event to be invoked. After 10 seconds, we give up
        synchronized (waitingHandler) {
            try {
                waitingHandler.wait(10000);
            } catch (InterruptedException ex) {
                fail("Interrupted while waiting for handler to finish");
            }
        }
    }

}
