package edu.gemini.aspen.gds.observationstate

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError

/**
 * Interface to be implemented by components wishing to  receive Observation state updates
 */
trait ObservationStateConsumer {
    def receiveStartObservation(label: DataLabel): Unit

    def receiveEndObservation(label: DataLabel, missingKeywords: Traversable[FitsKeyword], errorKeywords: Traversable[(FitsKeyword, CollectionError.CollectionError)]): Unit
}