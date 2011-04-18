package edu.gemini.aspen.gds.keywordssets.factory

import edu.gemini.aspen.gds.keywordssets.KeywordActorsFactory
import edu.gemini.aspen.giapi.data.Dataset
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Component}
import edu.gemini.epics.EpicsReader

@Component
@Instantiate
class StartObservationFactory(@Requires epicsService:EpicsReader) extends KeywordActorsFactory {

    def startObservationActors(dataSet: Dataset) = null
}