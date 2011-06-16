package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

/**
 * Trait for objects that can provide a set of Actors
 * that can in turn retrieve keyword values
 */
trait KeywordActorsFactory {

  /**
   * Request the factory to create and start actors required for the given observation event
   */
  def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel): List[KeywordValueActor]

  /**
   * Passes the global GDS configuration along
   */
  def configure(configuration: List[GDSConfiguration])
}