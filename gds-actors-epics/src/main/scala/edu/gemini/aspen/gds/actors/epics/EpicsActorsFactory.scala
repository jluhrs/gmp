package edu.gemini.aspen.gds.actors.epics

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.epics.EpicsReader
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Provides, Component}
import edu.gemini.aspen.gds.actors.{KeywordActorsFactory, KeywordValueActor}
import edu.gemini.aspen.gds.keywordssets.configuration.GDSConfiguration

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    // Use dummy configuration
    var conf: List[GDSConfiguration] = List()

    override def startAcquisitionActors(dataLabel: DataLabel) = {
        // There must be a cleaner way to do this
        conf map {
            case GDSConfiguration(_,_,keyword,_,_,_,_,channelName,_,_) => new EpicsValuesActor(epicsReader, new FitsKeyword(keyword.name), channelName.name)
        }
    }

    override def endAcquisitionActors(dataLabel: DataLabel) = List()

    override def configure(configuration:List[GDSConfiguration]) {
        conf = configuration filter { _.subsystem.name == "EPICS"}
    }
}