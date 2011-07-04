package edu.gemini.aspen.gds.observationstate

import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}


/**
 * Trait to be required by ObservationStateImpl to publish observation state changes
 */
trait ObservationStatePublisher {
    def publishStartObservation(label: DataLabel): Unit

    def publishEndObservation(label: DataLabel, missingKeywords: Traversable[FitsKeyword], errorKeywords: Traversable[(FitsKeyword, CollectionError.CollectionError)]): Unit
}




