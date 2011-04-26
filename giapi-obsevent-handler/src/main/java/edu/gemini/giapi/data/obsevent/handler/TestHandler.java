package edu.gemini.giapi.data.obsevent.handler;

import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.DataLabel;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple test handler for observation events
 */
public class TestHandler implements ObservationEventHandler {

    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());
    public void onObservationEvent(ObservationEvent event, DataLabel dataLabel) {
          
        LOG.info(Thread.currentThread().getId() + ": received " + event + "/" + dataLabel.getName());
        try {
            Thread.sleep(Thread.currentThread().getId() * 10);
        } catch (InterruptedException e) {
            LOG.log(Level.WARNING, "Thread interrupted", e);
        }
        LOG.info("Terminating thread " + Thread.currentThread().getId());
    }
}
