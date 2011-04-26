package edu.gemini.aspen.giapi.data;

/**
 * A handler that will be invoked whenever a new Ancillary File event
 * arrives.
 */
public interface AncillaryFileEventHandler {

    /**
     * Invoked when a new ancillary file event arrives. The framework
     * invokes this method in a separate thread.
     * @param filename name of the ancillary file associated to this event
     * @param dataLabel dataLabel associated to the ancillary file
     */
    void onAncillaryFileEvent(String filename, DataLabel dataLabel);
}
