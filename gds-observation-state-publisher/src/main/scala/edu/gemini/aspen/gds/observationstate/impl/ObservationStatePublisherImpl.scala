package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher

/**
 * Component that publishes Observation state changes
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationStatePublisher]))
class ObservationStatePublisherImpl extends ObservationStatePublisher {
    def publishStartObservation(label: DataLabel) {}

    def publishEndObservation(label: DataLabel, missingKeywords: Traversable[FitsKeyword], errorKeywords: Traversable[(FitsKeyword, CollectionError.CollectionError)]) {}
}
