package edu.gemini.aspen.gds.status

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}

/**
 * Factory of Actors that can retrieve instrument status
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[KeywordActorsFactory]))
class InstrumentStatusActorsFactory(@Requires statusDB: StatusDatabaseService) extends AbstractKeywordActorsFactory {
  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    configurationsForEvent(obsEvent) map {
      new InstrumentStatusActor(statusDB, _)
    }
  }

  override def getSource = KeywordSource.STATUS

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
    LOG.info("InstrumentStatusActorFactory started")
  }
}