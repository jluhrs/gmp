package edu.gemini.aspen.gds.observationstate

import java.time.Duration

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.{CollectedValue, CollectionError}
import edu.gemini.aspen.gds.api.fits.FitsKeyword

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
  def endObservation(label: DataLabel, writeTime: Long, collectedValues: Traversable[CollectedValue[_]]): Unit

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

  /**
   * Register that a dataLabel failed and give a cause
   */
  def registerError(label: DataLabel, cause: String): Unit
}