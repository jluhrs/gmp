package edu.gemini.aspen.gds.epics

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import scala.collection._
import edu.gemini.epics.{ReadOnlyClientEpicsChannel, NewEpicsReader, EpicsException}
import mutable.HashMap
import edu.gemini.aspen.gds.api.{Channel, GDSConfiguration, AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class NewEpicsActorsFactory(@Requires epicsReader: NewEpicsReader) extends AbstractKeywordActorsFactory {
  private val channels: mutable.Map[Channel, ReadOnlyClientEpicsChannel[_]] = new HashMap[Channel, ReadOnlyClientEpicsChannel[_]]

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    val (single, multiple) = actorsConfiguration filter {
      _.event.name == obsEvent.name
    } filter {
      c => channels.get(c.channel) exists {
        _.isValid
      }
    } groupBy {
      c => c.channel.name
    } partition {
      case (k, v) => v.size == 1
    }

    val singleActors = single.values.map {
      c => new NewEpicsValuesActor(channels(c.head.channel), c.head)
    }

    val arrayActors = multiple.values.map {
      c => new NewEpicsArrayValuesActor(channels(c.head.channel), c)
    }

    singleActors ++ arrayActors toList
  }

  override def configure(configuration: immutable.List[GDSConfiguration]) {
    super.configure(configuration)
    actorsConfiguration foreach {
      c => {
        try {
          channels.put(c.channel, epicsReader.getChannelAsync(c.channel.name))
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