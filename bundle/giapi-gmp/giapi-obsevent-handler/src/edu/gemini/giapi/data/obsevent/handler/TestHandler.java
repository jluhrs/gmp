package edu.gemini.giapi.data.obsevent.handler;

import edu.gemini.aspen.gmp.data.ObservationEventHandler;
import edu.gemini.aspen.gmp.data.ObservationEvent;
import edu.gemini.aspen.gmp.data.Dataset;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple test handler for observation events
 */
public class TestHandler implements ObservationEventHandler {

    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());
    public void onObservationEvent(ObservationEvent event, Dataset dataset) {
          
        LOG.info(Thread.currentThread().getId() + ": received " + event + "/" + dataset.getName());
        try {
            Thread.sleep(Thread.currentThread().getId() * 10);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Thread interrupted", e);
        }
        LOG.info("Terminating thread " + Thread.currentThread().getId());
    }
}
