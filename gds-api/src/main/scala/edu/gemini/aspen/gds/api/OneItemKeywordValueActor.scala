package edu.gemini.aspen.gds.api

import java.util.logging.{Logger, Level}
import scala.collection._

/**
 * Abstract class extending a KeywordValueActor adding some useful methods
 *
 * Actors reading from different sources can extend this class to simplify building those actors */
abstract class OneItemKeywordValueActor(private val config: GDSConfiguration) extends KeywordValueActor {
  protected val LOG = Logger.getLogger(this.getClass.getName)

  // Add some inner values to simplify code in the implementations
  protected val fitsKeyword = config.keyword
  protected val isMandatory = config.isMandatory
  protected val fitsComment = config.fitsComment.value
  protected val headerIndex = config.index.index
  protected val sourceChannel = config.channel.name
  protected val defaultValue = config.nullValue.value
  protected val dataType = config.dataType
  protected val arrayIndex = config.arrayIndex.value

  override def exceptionHandler = {
    case e: Exception => {
      LOG log (Level.SEVERE, "Unhandled exception while collecting data item", e)
      reply(immutable.List(ErrorCollectedValue(fitsKeyword, CollectionError.GenericError, fitsComment, headerIndex)))
    }
  }

  /**
   * Method to convert a value read from a given source to the type requested in the configuration */
  protected def valueToCollectedValue(value: Any): CollectedValue[_] = dataType match {
    // Anything can be converted to a string
    case DataType("STRING") => CollectedValue(fitsKeyword, value.toString, fitsComment, headerIndex)
    // Any number can be converted to a double
    case DataType("DOUBLE") => newDoubleCollectedValue(value)
    // Any number can be converted to a int
    case DataType("INT") => newIntCollectedValue(value)
    // Any number can be converted to a int
    case DataType("BOOLEAN") => newBooleanCollectedValue(value)
    // this should not happen
    case _ => newMismatchError
  }

  private def newDoubleCollectedValue(value: Any):CollectedValue[_] = value match {
    case x: java.lang.Number => CollectedValue(fitsKeyword, x.doubleValue, fitsComment, headerIndex)
    case _ => newMismatchError
  }

  private def collectedValueFromInt(value: Int):CollectedValue[_] = CollectedValue(fitsKeyword, value, fitsComment, headerIndex)

  private def newIntCollectedValue(value: Any) = value match {
    case x: java.lang.Long => {
      LOG.warning("Possible loss of precision converting " + x + " to integer")
      collectedValueFromInt(x.intValue)
    }
    case x: java.lang.Integer => collectedValueFromInt(x.intValue)
    case x: java.lang.Short => collectedValueFromInt(x.intValue)
    case x: java.lang.Byte => collectedValueFromInt(x.intValue)
    case _ => newMismatchError
  }

  private def newBooleanCollectedValue(value: Any) = value match {
    case x: java.lang.Number => CollectedValue(fitsKeyword, x.doubleValue() != 0, fitsComment, headerIndex)
    case x: String if x.equalsIgnoreCase("false") || x.equalsIgnoreCase("f") || x.equalsIgnoreCase("0") || x.isEmpty => CollectedValue(fitsKeyword, false, fitsComment, headerIndex)
    case x: String => CollectedValue(fitsKeyword, true, fitsComment, headerIndex)
    case x: Boolean => CollectedValue(fitsKeyword, x, fitsComment, headerIndex)
    case _ => newMismatchError
  }

  private def newMismatchError = ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)
}
