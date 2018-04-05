package edu.gemini.aspen.gds.properties


import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource, KeywordValueActor}
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}

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