package edu.gemini.aspen.gds.actors

import scala.actors.Actor
import Actor._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api._

class DummyActorsFactory extends KeywordActorsFactory {
    override def buildPrepareObservationActors(dataLabel: DataLabel) = buildStartAcquisitionActors(dataLabel)
    override def buildEndAcquisitionActors(dataLabel: DataLabel) = buildStartAcquisitionActors(dataLabel)
    override def buildStartAcquisitionActors(dataLabel: DataLabel) = {
        val dummyActor = new KeywordValueActor{
          override def collectValues: List[CollectedValue] = {
              List(CollectedValue(new FitsKeyword("KEYWORD1"), "", "", 0))
          }
        }
        dummyActor :: Nil
    }

    def configure(configuration:List[GDSConfiguration]) {}
}

