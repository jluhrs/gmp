package edu.gemini.aspen.gds.keywordssets

import edu.gemini.aspen.giapi.data.Dataset
import scala.actors.Actor
import Actor._

class DummyActorsFactory extends KeywordActorsFactory {
    def startObservationActors(dataSet: Dataset): List[Actor] = {
        val dummyActor = actor {
            react {
                case Collect => reply("collected")
            }
        }
        dummyActor :: Nil
    }
}

