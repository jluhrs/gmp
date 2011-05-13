package edu.gemini.aspen.gds.actors

import scala.actors.Actor
import Actor._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.{Collect, KeywordActorsFactory, GDSConfiguration, CollectedValue}

class DummyActorsFactory extends KeywordActorsFactory {
    override def buildInitializationActors(programID:String, dataLabel:DataLabel) = List()

    override def buildStartAcquisitionActors(dataLabel: DataLabel) = {
        val dummyActor = actor {
            react {
                case Collect => reply(List(CollectedValue(new FitsKeyword("KEYWORD1"), "", "", 0)))
            }
        }
        dummyActor :: Nil
    }

    override def buildEndAcquisitionActors(dataLabel: DataLabel) = List()

    def configure(configuration:List[GDSConfiguration]) {}
}

