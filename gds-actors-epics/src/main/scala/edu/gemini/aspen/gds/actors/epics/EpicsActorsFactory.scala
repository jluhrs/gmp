package edu.gemini.aspen.gds.actors.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    // Use dummy configuration
    var conf: List[GDSConfiguration] = List()

    override def startAcquisitionActors(dataLabel: DataLabel) = {
        conf filter {_.event.name == ObservationEvent.OBS_START_ACQ.toString} map {
            case config:GDSConfiguration => new EpicsValuesActor(epicsReader, config)
        }
    }

    override def endAcquisitionActors(dataLabel: DataLabel) = {
        conf filter {_.event.name == ObservationEvent.OBS_END_ACQ.toString} map {
            case config:GDSConfiguration => new EpicsValuesActor(epicsReader, config)
        }
    }

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