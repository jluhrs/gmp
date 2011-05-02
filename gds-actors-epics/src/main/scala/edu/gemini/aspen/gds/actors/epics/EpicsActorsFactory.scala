package edu.gemini.aspen.gds.actors.epics

import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.actors.KeywordActorsFactory
import edu.gemini.aspen.gds.keywordssets.configuration.GDSConfiguration
import org.apache.felix.ipojo.annotations._

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    // Use dummy configuration
    var conf: List[GDSConfiguration] = List()

    override def startAcquisitionActors(dataLabel: DataLabel) = {
        // There must be a cleaner way to do this
        conf map {
            case config:GDSConfiguration => new EpicsValuesActor(epicsReader, config)
        }
    }

    override def endAcquisitionActors(dataLabel: DataLabel) = List()

    override def configure(configuration:List[GDSConfiguration]) {
        conf = configuration filter { _.subsystem.name == "EPICS"}
        // Bind all required channels
        conf map { x =>
            epicsReader.bindChannel(x.channel.name)
        }
    }

    @Invalidate
    def unbindAllChannels() {
        // TODO remove all the bound channels
    }
}