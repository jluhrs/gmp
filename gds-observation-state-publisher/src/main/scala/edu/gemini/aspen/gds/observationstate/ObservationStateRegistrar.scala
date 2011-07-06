package edu.gemini.aspen.gds.observationstate

import org.scala_tools.time.TypeImports._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError

/**
 * Interface to be required by GDSObsEventHandler, to register data for the Observation state
 */
trait ObservationStateRegistrar {
    /**
     * Register the start of an observation(OBS_PREP received)
     */
    def startObservation(label: DataLabel): Unit

    /**
     * Register the end of an observation(OBS_END_DSET_WRITE received and/or FITS file updated)
     */
    def endObservation(label: DataLabel): Unit

    /**
     * Register the timing info for processing the different obs events
     */
    def registerTimes(label: DataLabel, times: Traversable[(AnyRef, Option[Duration])]): Unit

    /**
     * Register which keywords couldn't be collected because of errors
     */
    def registerCollectionError(label: DataLabel, errors: Traversable[(FitsKeyword, CollectionError.CollectionError)]): Unit

    /**
     * Register which keywords weren't collected
     */
    def registerMissingKeyword(label: DataLabel, keywords: Traversable[FitsKeyword]): Unit
}