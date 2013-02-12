package edu.gemini.aspen.gds.epics

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import scala.collection._
import edu.gemini.epics.{ReadOnlyClientEpicsChannel, EpicsReader, EpicsException}
import mutable.HashMap
import edu.gemini.aspen.gds.api.{Channel, GDSConfiguration, AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}
import gov.aps.jca.CAException

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class EpicsActorsFactory(@Requires epicsReader: EpicsReader) extends AbstractKeywordActorsFactory {
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
      c => new EpicsValuesActor(channels(c.head.channel), c.head)
    }

    val arrayActors = multiple.values.map {
      c => new EpicsArrayValuesActor(channels(c.head.channel), c)
    }

    (singleActors ++ arrayActors).toList
  }

  override def configure(configuration: immutable.List[GDSConfiguration]) {
    val oldConfig=actorsConfiguration.toSet
    super.configure(configuration)
    val newConfig = actorsConfiguration.toSet

    val addedElements = newConfig -- oldConfig
    val removedElements = oldConfig -- newConfig

    addedElements.foreach {
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

    removedElements.foreach {
      c => {
        try {
          channels.remove(c.channel).foreach  {_.destroy()}
        } catch {
          case ex: CAException => {
            LOG.severe(ex.getMessage)
          }
        }
      }
    }
  }

  override def getSource = KeywordSource.EPICS

  @Invalidate
  def unbindAllChannels() {
    channels.values.foreach {
      _.destroy()
    }
    channels.clear()
  }
}