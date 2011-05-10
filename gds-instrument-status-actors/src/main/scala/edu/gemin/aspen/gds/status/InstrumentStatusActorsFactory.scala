package edu.gemini.aspen.gds.status

import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemin.aspen.gds.status.InstrumentStatusActor

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class InstrumentStatusActorsFactory() extends KeywordActorsFactory {
    // Use dummy configuration
    var conf: List[GDSConfiguration] = List()

    override def startAcquisitionActors(dataLabel: DataLabel) = {
        conf filter {_.event.name == ObservationEvent.OBS_START_ACQ.toString} map {
            case config:GDSConfiguration => new InstrumentStatusActor(config)
        }
    }

    override def endAcquisitionActors(dataLabel: DataLabel) = {
        conf filter {_.event.name == ObservationEvent.OBS_END_ACQ.toString} map {
            case config:GDSConfiguration => new InstrumentStatusActor(config)
        }
    }

    override def configure(configuration:List[GDSConfiguration]) {
        conf = configuration filter { _.subsystem.name == "STATUS"}
//        // Bind all required channels
//        conf map { x =>
//            epicsReader.bindChannel(x.channel.name)
//        }
    }

    @Invalidate
    def unbindAllChannels() {
        // TODO remove all the bound channels
    }
}