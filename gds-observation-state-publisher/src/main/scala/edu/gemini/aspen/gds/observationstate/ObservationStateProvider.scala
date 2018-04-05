package edu.gemini.aspen.gds.observationstate

import java.time.{Duration, LocalDateTime}

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.gds.api.fits.FitsKeyword

/**
 * Interface to be required by somebody that wants to poll for data for the Observation state
 */
trait ObservationStateProvider {
  /**
   * Returns a traversable with all the observations that have started but which have not ended or the FITS file is not written entirely
   */
  def getObservationsInProgress: Traversable[DataLabel]

  /**
   * Returns true if the observation has missing or error keywords
   */
  def isInError(label: DataLabel): Option[Boolean]

  /**
   * Returns true if the observation processing has failed. e.g. file errors
   */
  def isFailed(label: DataLabel): Option[Boolean]

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
   * Returns timing information for the processing of the observation events for a given DataLabel
   */
  def getTimestamp(label: DataLabel): Option[LocalDateTime]

  /**
   * Returns the last DataLabel processed. A DataLabel timestamp is created upon first hearing of an observation,
   * not necessarily when receiving a startObservation or endObservation.
   */
  def getLastDataLabel: Option[DataLabel]

  /**
   * Returns the N last DataLabels processed. A DataLabel timestamp is created upon first hearing of an observation,
   * not necessarily when receiving a startObservation or endObservation.
   */
  def getLastDataLabel(n: Int): Traversable[DataLabel]
}