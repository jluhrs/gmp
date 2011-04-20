package edu.gemini.aspen.gds.keywordssets.factory

import edu.gemini.aspen.giapi.data.Dataset
import edu.gemini.epics.EpicsReader
import actors.Actor
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywordssets.KeywordActorsFactory
import xml.{Elem, XML}

@Component
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class ObservationStartActorsFactory(@Requires epicsReader: EpicsReader, @Property(name="startObservationFactory", value="INVALID", mandatory = true) fileName: String) extends KeywordActorsFactory {
    val actorsBuilder = new ConfigBasedActorBuilder(XML.loadFile(fileName))

    def startObservationActors(dataSet: Dataset): List[Actor] = {
        //new EpicsValuesActor(epicsReader) :: Nil
        actorsBuilder.buildActors
    }

    @Validate()
    def validate() = {println("new validate " + epicsReader + " " + fileName)}
}

class ConfigBasedActorBuilder(xmlConfig: Elem) {
    def buildActors: List[Actor] = {
        val a = xmlConfig \ "actor"
        a foreach { node =>
            //case <actor keywords="{keywords}" class="{clazz}">{contents}</actor> => println(contents)
            val keywords = node \ "@keywords"
            val clazz = node \ "@class"
            println (keywords + " " + clazz)
            val constructor = Class.forName(clazz.text).getConstructors
            constructor foreach { c =>
                c.getParameterTypes foreach {
                    println _
                }
            }
        }

        Nil
    }
}