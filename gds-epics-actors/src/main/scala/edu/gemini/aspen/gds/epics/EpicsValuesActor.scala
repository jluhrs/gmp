package edu.gemini.aspen.gds.epics

import edu.gemini.aspen.gds.api._
import org.joda.time.DateTime
import org.scala_tools.time.Imports._
import edu.gemini.epics.{EpicsException, EpicsReader}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  private val MAX_ATTEMPTS = 3;

  /** Method to retry to read an epics channel if it fails once */
  private def readChannelValue(attempt: Int): Option[AnyRef] = {
    try {
      Option(epicsReader.getValue(sourceChannel))
    } catch {
      case e: EpicsException => {
        attempt match {
          case 0 => None
          case _ => readChannelValue(attempt - 1)
        }
      }
    }
  }

  override def collectValues(): List[CollectedValue[_]] = {
    val start = new DateTime
    val readValue = readChannelValue(MAX_ATTEMPTS)
    val end = new DateTime
    LOG.fine("Reading EPICS channel " + sourceChannel + " took " + (start to end).toDurationMillis + " [ms]")

    try {
      readValue map (convertCollectedValue) orElse (defaultCollectedValue) toList
    } catch {
      case e: MatchError => {
        LOG.warning("Data for " + fitsKeyword + " keyword was not of the type specified in config file.")
        List(ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex))
      }
    }
  }

  def convertCollectedValue(epicsValue: AnyRef): CollectedValue[_] = {
    val valueArray = epicsValue.asInstanceOf[Array[_]]
    if (arrayIndex < valueArray.length) {
      valueToCollectedValue(valueArray(arrayIndex))
    } else {
      ErrorCollectedValue(fitsKeyword, CollectionError.ArrayIndexOutOfBounds, fitsComment, headerIndex)
    }
  }
}
