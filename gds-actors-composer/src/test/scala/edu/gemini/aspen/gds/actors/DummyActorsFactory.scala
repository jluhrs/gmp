package edu.gemini.aspen.gds.actors

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.{ObservationEvent, FitsKeyword, DataLabel}

class DummyActorsFactory extends KeywordActorsFactory {
    override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
        val dummyActor = new KeywordValueActor {
            override def collectValues: List[CollectedValue[_]] = {
                List(CollectedValue(new FitsKeyword("KEYWORD1"), "", "", 0))
            }
        }
        dummyActor :: Nil
    }

    override def configure(configuration: List[GDSConfiguration]) {}
}

