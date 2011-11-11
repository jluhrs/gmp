package edu.gemini.aspen.gds.epics

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.api.{GDSConfiguration, AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}
import scala.collection._
import edu.gemini.epics.{ReadOnlyClientEpicsChannel, NewEpicsReader, EpicsException}
import mutable.HashMap

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class NewEpicsActorsFactory(@Requires epicsReader: NewEpicsReader) extends AbstractKeywordActorsFactory {
  private val channels: mutable.Map[GDSConfiguration, ReadOnlyClientEpicsChannel[_]] = new HashMap[GDSConfiguration, ReadOnlyClientEpicsChannel[_]]

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    actorsConfiguration filter {
      _.event.name == obsEvent.name
    } filter {
      c => channels.get(c) exists {
        _.isValid
      }
    } map {
      c => {
        new NewEpicsValuesActor(channels(c), c)
      }
    }
  }

  override def configure(configuration: immutable.List[GDSConfiguration]) {
    super.configure(configuration)
    actorsConfiguration foreach {
      c => {
        try {
          channels.put(c, epicsReader.getChannelAsync(c.channel.name))
        } catch {
          case ex: EpicsException => {
            LOG.severe(ex.getMessage)
          }
        }
      }
    }
  }

  override def getSource = KeywordSource.EPICS

  @Invalidate
  def unbindAllChannels() {
    channels.values foreach {
      _.destroy()
    }
    channels.clear()
  }
}