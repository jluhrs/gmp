package edu.gemini.aspen.gds.status

import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource}
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}
import edu.gemini.aspen.giapi.status.StatusDatabaseService

/**
 * Factory of Actors that can retrieve instrument status
 */
class InstrumentStatusActorsFactory(statusDB: StatusDatabaseService) extends AbstractKeywordActorsFactory {
  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    configurationsForEvent(obsEvent).map {
      new InstrumentStatusActor(statusDB, _)
    }
  }

  override def getSource = KeywordSource.STATUS

  /**
   * Filters out only the configuration events relevant for a given observation event
   */
  private def configurationsForEvent(e: ObservationEvent) = {
    actorsConfiguration.filter {
      _.event.name == e.toString
    }
  }

}