package edu.gemini.aspen.gds.actors.epics

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.epics.EpicsReader
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Provides, Component}
import edu.gemini.aspen.gds.actors.{KeywordActorsFactory, KeywordValueActor}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    // Use dummy configuration
    val conf: Map[FitsKeyword, String] = Map(
        new FitsKeyword("HUMIDITY") -> "ws:wsFilter.VALO"
    )

    override def startAcquisitionActors(dataLabel: DataLabel) = {
        // There must be a cleaner way to do this
        var actorsList:List[KeywordValueActor] = List()
        conf map {
            case (keyword, channel) =>
                actorsList = new EpicsValuesActor(epicsReader, keyword, channel) :: actorsList
        }
        actorsList
    }

    override def endAcquisitionActors(dataLabel: DataLabel) = List()
}