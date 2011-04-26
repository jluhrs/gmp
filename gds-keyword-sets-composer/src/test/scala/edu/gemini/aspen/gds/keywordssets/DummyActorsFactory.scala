package edu.gemini.aspen.gds.keywordssets

import scala.actors.Actor
import Actor._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}

class DummyActorsFactory extends KeywordActorsFactory {
    def startObservationActors(dataLabel: DataLabel): List[Actor] = {
        val dummyActor = actor {
            react {
                case Collect => reply(List(CollectedValue(new FitsKeyword("KEYWORD1"), "", "")))
            }
        }
        dummyActor :: Nil
    }
}

