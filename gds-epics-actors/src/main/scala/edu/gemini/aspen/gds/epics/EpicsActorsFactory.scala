package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends KeywordActorsFactory {
    // Use dummy configuration

    override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
        actorsConfiguration filter {
            _.event.name == obsEvent.name
        } map {
            c => {
                epicsReader.bindChannel(c.channel.name)
                new EpicsValuesActor(epicsReader, c)
            }
        }
    }

    override def getSource = KeywordSource.EPICS

    @Invalidate
    def unbindAllChannels() {
        // Unbind all required channels
        actorsConfiguration map {
            c => epicsReader.unbindChannel(c.channel.name)
        }
    }
}