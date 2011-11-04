package edu.gemini.aspen.gds.epics

import java.util.logging.Logger
import edu.gemini.aspen.gds.api._
import edu.gemini.epics.NewEpicsReader
import edu.gemini.epics.api.ReadOnlyChannel

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class NewEpicsValuesActor(epicsReader: NewEpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val readValue = if (arrayIndex > 0) {
      Option(epicsReader.getChannel(sourceChannel)) map (extractEpicsItem)
    } else {
      Option(epicsReader.getChannel(sourceChannel)) map (extractEpicsItemArray)
    }
    readValue map valueToCollectedValue toList
  }

  def extractEpicsItem(epicsValue: ReadOnlyChannel[_]) = {
    epicsValue.getFirst
  }

  def extractEpicsItemArray(epicsValue: ReadOnlyChannel[_]) = {
    epicsValue.getAll().get(arrayIndex)
  }

}
