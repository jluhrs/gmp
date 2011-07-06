package edu.gemini.aspen.gds.observationstate

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError

/**
 * Interface to be implemented by components wishing to  receive Observation state updates
 */
trait ObservationStateConsumer {
    /**
     * Will be called when when OBS_PREP obs event arrives
     */
    def receiveStartObservation(label: DataLabel): Unit

    /**
     * Will be called when OBS_WRITE_DSET_END obs event arrives, and/or? the FITS file has been updated
     */
    def receiveEndObservation(label: DataLabel, missingKeywords: Traversable[FitsKeyword], errorKeywords: Traversable[(FitsKeyword, CollectionError.CollectionError)]): Unit
}