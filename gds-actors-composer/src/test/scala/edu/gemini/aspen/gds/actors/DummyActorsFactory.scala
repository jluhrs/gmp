package edu.gemini.aspen.gds.actors

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.{ObservationEvent, FitsKeyword, DataLabel}
import scala.collection._

class DummyActorsFactory extends AbstractKeywordActorsFactory {
  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    val dummyActor = new KeywordValueActor {
      override def collectValues: immutable.List[CollectedValue[_]] = immutable.List(CollectedValue(new FitsKeyword("KEYWORD1"), "", "", 0))
    }
    immutable.List(dummyActor)
  }

  override def configure(configuration: List[GDSConfiguration]) {}
}

