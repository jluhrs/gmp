package edu.gemini.giapi.data.fileevent.handler;

import edu.gemini.aspen.gmp.data.IntermediateFileEventHandler;
import edu.gemini.aspen.gmp.data.AncillaryFileEventHandler;
import edu.gemini.aspen.gmp.data.Dataset;

import java.util.logging.Logger;

/**
 * Simple test to show how to implement an intermediate/ancillary file handler
 * and use it inside OSGi. 
 */
public class TestHandler implements IntermediateFileEventHandler, AncillaryFileEventHandler {

    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());


    public void onIntermediateFileEvent(String filename, Dataset dataset, String hint) {
        LOG.info("Intermediate file Event received: Filename [" + filename + "], Dataset [" +
        dataset + "], hint [" + hint +"]");
    }

    public void onAncillaryFileEvent(String filename, Dataset dataset) {
        LOG.info("Ancillary file Event received: Filename [" + filename + "], Dataset [" +
        dataset + "]");
    }
}
