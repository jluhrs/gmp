package edu.gemini.aspen.gds.constant

import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource}
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}

/**
 * Factory of Actors that can retrieve instrument status
 */
class ConstantActorsFactory extends AbstractKeywordActorsFactory {

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel): List[ConstantActor] = {
    new ConstantActor(configurationsForEvent(obsEvent)) :: Nil
  }

  override def getSource: KeywordSource.Value = KeywordSource.CONSTANT

  /**
   * Filters out only the configuration events relevant for a given observation event
   */
  private def configurationsForEvent(e: ObservationEvent) = {
    actorsConfiguration filter {
      _.event.name == e.toString
    }
  }

}