package edu.gemini.aspen.gds.seqexec


import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.properties.PropertiesValuesActor
import edu.gemini.aspen.gds.api.{KeywordValueActor, AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class PropertiesActorsFactory extends AbstractKeywordActorsFactory {

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel):List[KeywordValueActor] = {
    actorsConfiguration filter {
      _.event.name == obsEvent.name
    } map {
      c => new PropertiesValuesActor(c)
    }
  }

  override def getSource = KeywordSource.PROPERTY

}