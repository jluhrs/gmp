package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api.{DataType, OneItemKeywordValueActor, GDSConfiguration, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val readValue = Option(epicsReader.getValue(sourceChannel))

    readValue map (collectedValue) orElse (defaultCollectedValue) toList
  }

  def collectedValue(epicsValue: AnyRef): CollectedValue[_] = {
    dataType match {
      case DataType("STRING") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[String]](0), fitsComment, headerIndex)
      case DataType("DOUBLE") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](0), fitsComment, headerIndex)
      case DataType("INT") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Int]](0), fitsComment, headerIndex)
    }
    // TODO: Take care of arrays
  }

}
