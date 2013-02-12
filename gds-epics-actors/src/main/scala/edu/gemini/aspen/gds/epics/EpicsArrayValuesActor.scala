package edu.gemini.aspen.gds.epics

import edu.gemini.aspen.gds.api._
import edu.gemini.epics.api.ReadOnlyChannel
import java.util.logging.{Level, Logger}

/**
 * Actor that collects several items from the same EPICS array
 */
class EpicsArrayValuesActor(channel: ReadOnlyChannel[_], configurations: Traversable[GDSConfiguration]) extends KeywordValueActor {
  protected val LOG = Logger.getLogger(this.getClass.getName)

  if (!(configurations forall {
    c => c.channel.equals(configurations.head.channel)
  })) {
    throw new IllegalArgumentException("All sources must be equal")
  }
  if (!(configurations forall {
    c => c.dataType.equals(configurations.head.dataType)
  })) {
    throw new IllegalArgumentException("All data types must be equal")
  }

  override def exceptionHandler = {
    case e: Exception => {
      LOG log(Level.SEVERE, "Unhandled exception while collecting data item", e)
      reply(configurations.map {
        c => ErrorCollectedValue(c.keyword, CollectionError.GenericError, c.fitsComment.value, c.index.index)
      }.toList)
    }
  }

  override def collectValues(): List[CollectedValue[_]] = {
    val values = channel.getAll

    configurations.map {
      c =>
        try {
          new EpicsOneValueActor(c, values.get(c.arrayIndex.value).asInstanceOf[AnyRef]).collectValues().head
        } catch {
          case ex: IndexOutOfBoundsException => {
            ErrorCollectedValue(c.keyword, CollectionError.ArrayIndexOutOfBounds, c.fitsComment.value, c.index.index)
          }
        }
    }.toList
  }


  /**
   * Internal class for converting value
   */
  private class EpicsOneValueActor(configuration: GDSConfiguration, value: AnyRef) extends OneItemKeywordValueActor(configuration) {
    override def collectValues(): List[CollectedValue[_]] = {
      Option(value).map(valueToCollectedValue).toList
    }
  }

}
