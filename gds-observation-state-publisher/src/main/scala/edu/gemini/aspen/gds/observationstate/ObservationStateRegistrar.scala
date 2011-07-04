package edu.gemini.aspen.gds.observationstate

import org.scala_tools.time.TypeImports._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError

/**
 * Interface to be required by GDSObsEventHandler, to register data for the Observation state
 */
trait ObservationStateRegistrar {
    def startObservation(label: DataLabel): Unit

    def endObservation(label: DataLabel): Unit

    def registerTimes(label: DataLabel, times: Traversable[(AnyRef, Option[Duration])]): Unit

    def registerCollectionError(label: DataLabel, keyword: FitsKeyword, error: CollectionError.CollectionError): Unit

    def registerMissingKeyword(label: DataLabel, keyword: FitsKeyword): Unit
}