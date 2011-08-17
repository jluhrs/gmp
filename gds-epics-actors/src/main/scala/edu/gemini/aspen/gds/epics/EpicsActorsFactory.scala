package edu.gemini.aspen.gds.epics

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.epics.{EpicsException, EpicsReader}
import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends AbstractKeywordActorsFactory {

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    actorsConfiguration filter {
      _.event.name == obsEvent.name
    } filter {
      c => {
        try {
          epicsReader.bindChannel(c.channel.name)
          true //if binding doesn't throw, we keep it
        } catch {
          case ex: EpicsException => {
            LOG.severe(ex.getMessage)
            false //if binding throws, we log it and discard it
          }
        }
      }
    } map {
      c => {
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