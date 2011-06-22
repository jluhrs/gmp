package edu.gemini.aspen.gds.epics

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

    override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
        conf filter {
            _.event.name == obsEvent.name
        } map {
            c => {
                epicsReader.bindChannel(c.channel.name)
                new EpicsValuesActor(epicsReader, c)
            }
        }
    }

    override def configure(configuration: List[GDSConfiguration]) {
        conf = configuration filter {
            _.subsystem.name == "EPICS"
        }
    }

    @Invalidate
    def unbindAllChannels() {
        // Unbind all required channels
        conf map {
            c => epicsReader.unbindChannel(c.channel.name)
        }
    }
}