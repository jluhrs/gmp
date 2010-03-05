package edu.gemini.aspen.giapi.data.fileevents;

import edu.gemini.aspen.gmp.data.AncillaryFileEventHandler;
import edu.gemini.aspen.gmp.data.IntermediateFileEventHandler;

/**
 * This interface defines a composite for {@link edu.gemini.aspen.gmp.data.AncillaryFileEventHandler}s.
 * and {@link edu.gemini.aspen.gmp.data.IntermediateFileEventHandler}
 * When any of the File Event occurs, all the registered handlers will be
 * invoked so they get and process the events. 
 */
public interface FileEventHandlerComposite extends AncillaryFileEventHandler, IntermediateFileEventHandler {


    /**
     * Add an ancillary file event handler to the composite
     * @param handler the ancillary file event handler to add
     */
    void addAncillaryFileEventHandler(AncillaryFileEventHandler handler);

    /**
     * Remove an ancillary file event handler to the composite
     * @param handler the ancillary file event handler to remove
     */

    void removeAncillaryFileEventHandler(AncillaryFileEventHandler handler);


    /**
     * Add an intermediate file event handler to the composite
     * @param handler the intermediate file event handler to add
     */
    void addIntermediateFileEventHandler(IntermediateFileEventHandler handler);

    /**
     * Remove an intermediate file event handler to the composite
     * @param handler the intermediate file event handler to remove
     */
    void removeIntermediateFileEventHandler(IntermediateFileEventHandler handler);

}
