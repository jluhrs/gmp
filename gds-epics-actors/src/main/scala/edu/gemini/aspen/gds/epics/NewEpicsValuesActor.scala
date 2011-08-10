package edu.gemini.aspen.gds.epics

import java.util.logging.Logger
import edu.gemini.aspen.gds.api._
import edu.gemini.epics.{EpicsChannelArray, EpicsChannel, NewEpicsReader}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class NewEpicsValuesActor(epicsReader: NewEpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val readValue = if (arrayIndex > 0) {
      Option(epicsReader.getChannel(sourceChannel)) map (extractEpicsItem)
    } else {
      Option(epicsReader.getArrayChannel(sourceChannel)) map (extractEpicsItemArray)
    }
    readValue map valueToCollectedValue toList
  }

  def extractEpicsItem(epicsValue: EpicsChannel[_]) = {
    epicsValue.getValue
  }

  def extractEpicsItemArray(epicsValue: EpicsChannelArray[_]) = {
    epicsValue.getValue()(arrayIndex)
  }

}
