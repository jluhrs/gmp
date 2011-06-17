package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api.{DataType, OneItemKeywordValueActor, GDSConfiguration, CollectedValue}
import java.util.logging.Logger

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  private val LOG = Logger.getLogger(this.getClass.getName)

  override def collectValues(): List[CollectedValue[_]] = {
    val readValue = Option(epicsReader.getValue(sourceChannel))
    try {
      readValue map (collectedValue) orElse (defaultCollectedValue) toList
    } catch {
      case e: ClassCastException => {
        LOG.warning("Data for " + fitsKeyword + " keyword was not of the type specified in config file.")
        Nil
      }
    }
  }

  def collectedValue(epicsValue: AnyRef): CollectedValue[_] = {
    dataType match {
      case DataType("STRING") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[_]](arrayIndex).toString, fitsComment, headerIndex)
      case DataType("DOUBLE") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](arrayIndex), fitsComment, headerIndex)
      case DataType("INT") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Int]](arrayIndex), fitsComment, headerIndex)
    }
    // TODO: Take care of arrays
  }

}
