package edu.gemini.aspen.giapi.data.fileevents;

import edu.gemini.aspen.giapi.data.AncillaryFileEventHandler;
import edu.gemini.aspen.giapi.data.IntermediateFileEventHandler;

/**
 * This interface combines both the
 * {@link edu.gemini.aspen.giapi.data.AncillaryFileEventHandler}.
 * and the {@link edu.gemini.aspen.giapi.data.IntermediateFileEventHandler}
 * in a single action, which is used by the File Event Listener  
 */
public interface FileEventAction extends AncillaryFileEventHandler, IntermediateFileEventHandler {

}
