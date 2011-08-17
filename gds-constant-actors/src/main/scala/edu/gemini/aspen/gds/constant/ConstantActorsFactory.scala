package edu.gemini.aspen.gds.constant

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import java.util.logging.Logger
import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}

/**
 * Factory of Actors that can retrieve instrument status
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class ConstantActorsFactory extends AbstractKeywordActorsFactory {

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    new ConstantActor(configurationsForEvent(obsEvent)) :: Nil
  }

  override def getSource = KeywordSource.CONSTANT

  /**
   * Filters out only the configuration events relevant for a given observation event
   */
  private def configurationsForEvent(e: ObservationEvent) = {
    actorsConfiguration filter {
      _.event.name == e.toString
    }
  }

  @Validate
  def start() {
    LOG.fine("ConstantActorFactory started")
  }
}