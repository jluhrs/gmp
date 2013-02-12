package edu.gemini.aspen.gds.epics

import edu.gemini.aspen.gds.api._
import edu.gemini.epics.api.ReadOnlyChannel

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(channel: ReadOnlyChannel[_], configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val collected = if (arrayIndex > 0) {
      val values = channel.getAll
      if (arrayIndex < values.size()) {
        Option(channel.getAll.get(arrayIndex)).map(valueToCollectedValue)
      } else {
        Option(ErrorCollectedValue(fitsKeyword, CollectionError.ArrayIndexOutOfBounds, fitsComment, headerIndex))
      }
    } else {
      Option(channel.getFirst).map(valueToCollectedValue)
    }
    collected.toList
  }
}
