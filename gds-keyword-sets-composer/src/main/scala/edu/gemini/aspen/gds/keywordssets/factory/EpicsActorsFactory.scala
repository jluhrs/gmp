package edu.gemini.aspen.gds.keywordssets.factory

import edu.gemini.aspen.gds.keywordssets.KeywordActorsFactory
import edu.gemini.aspen.giapi.data.{FitsKeyword, Dataset}
import edu.gemini.epics.EpicsReader
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Provides, Component}
import edu.gemini.aspen.gds.keywords.actors.EpicsValuesActor
import actors.Actor

@Component
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
@Instantiate
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    // Use dummy configuration
    val conf: Map[FitsKeyword, String] = Map(
        new FitsKeyword("HUMIDITY") -> "ws:wsFilter.VALO"
    )

    def startObservationActors(dataSet: Dataset) = {
        // There must be a cleaner way to do this
        var actorsList:List[Actor] = List()
        conf map {
            case (keyword, channel) =>
                actorsList = new EpicsValuesActor(epicsReader, keyword, channel) :: actorsList
        }
        actorsList
    }
}