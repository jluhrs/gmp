package edu.gemini.aspen.gds.observationstate

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError
import org.scala_tools.time.TypeImports._

/**
 * Interface to be required by somebody that wants to poll for data for the Observation state
 */
trait ObservationStateProvider {
    def getObservationsInProgress: Traversable[DataLabel]

    def getKeywordsInError(label: DataLabel): Traversable[(FitsKeyword, CollectionError.CollectionError)]

    def getMissingKeywords(label: DataLabel): Traversable[FitsKeyword]

    def getTimes(label: DataLabel): Map[AnyRef, Option[Duration]]
}