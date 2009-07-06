package edu.gemini.giapi.data.obsevent.handler;

import edu.gemini.aspen.gmp.data.ObservationEventHandler;
import edu.gemini.aspen.gmp.data.ObservationEvent;
import edu.gemini.aspen.gmp.data.Dataset;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Jun 29, 2009
 * Time: 6:24:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestHandler implements ObservationEventHandler {

    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());
    public void onObservationEvent(ObservationEvent event, Dataset dataset) {
          
        LOG.info(Thread.currentThread().getId() + ": received " + event + "/" + dataset.getName());
        try {
            Thread.sleep(Thread.currentThread().getId() * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        LOG.info("Terminating..." + Thread.currentThread().getId());
    }
}
