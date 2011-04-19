package edu.gemini.aspen.gds.keywordssets.factory

import edu.gemini.aspen.giapi.data.Dataset
import edu.gemini.epics.EpicsReader
import actors.Actor
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywordssets.KeywordActorsFactory
import annotation.target.{param, field}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class StartObservationFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    def startObservationActors(dataSet: Dataset): List[Actor] = {
        new EpicsValuesActor(epicsReader) :: Nil
    }

    @Validate()
    def validate() = {println("new validate " + epicsReader)}
}

class EpicsValuesActor(epicsReader: EpicsReader) extends Actor {
    def act() {
        println(epicsReader)
        reply("collected")
    }
}