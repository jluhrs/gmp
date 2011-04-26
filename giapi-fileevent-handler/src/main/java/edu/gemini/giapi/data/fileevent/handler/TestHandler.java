package edu.gemini.giapi.data.fileevent.handler;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.IntermediateFileEventHandler;
import edu.gemini.aspen.giapi.data.AncillaryFileEventHandler;

import java.util.logging.Logger;

/**
 * Simple test to show how to implement an intermediate/ancillary file handler
 * and use it inside OSGi. 
 */
public class TestHandler implements IntermediateFileEventHandler, AncillaryFileEventHandler {

    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());


    public void onIntermediateFileEvent(String filename, DataLabel dataLabel, String hint) {
        LOG.info("Intermediate file Event received: Filename [" + filename + "], DataLabel [" +
                dataLabel + "], hint [" + hint +"]");
    }

    public void onAncillaryFileEvent(String filename, DataLabel dataLabel) {
        LOG.info("Ancillary file Event received: Filename [" + filename + "], DataLabel [" +
                dataLabel + "]");
    }
}
