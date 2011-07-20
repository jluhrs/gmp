package edu.gemini.aspen.gds.observationstate

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError
import org.scala_tools.time.Imports._

/**
 * Interface to be required by somebody that wants to poll for data for the Observation state
 */
trait ObservationStateProvider {
    /**
     * Returns a traversable with all the observations that have started but which have not ended or the FITS file is not written entirely
     */
    def getObservationsInProgress: Traversable[DataLabel]

    /**
     * Returns the list of keywords that couldn't be collected because of an error
     */
    def getKeywordsInError(label: DataLabel): Traversable[(FitsKeyword, CollectionError.CollectionError)]

    /**
     * Returns the list of keywords that are listed in the config file, but will not be written to the FITS file.
     */
    def getMissingKeywords(label: DataLabel): Traversable[FitsKeyword]

    /**
     * Returns timing information for the processing of the observation events for a given DataLabel
     */
    def getTimes(label: DataLabel): Traversable[(AnyRef, Option[Duration])]

    /**
     * Returns the last DataLabel processed
     */
    def getLastDataLabel: Option[DataLabel]
}