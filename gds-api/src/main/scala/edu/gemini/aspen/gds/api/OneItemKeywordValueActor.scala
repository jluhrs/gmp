package edu.gemini.aspen.gds.api

import java.util.logging.{Level, Logger}

import edu.gemini.aspen.gds.api.fits.FitsKeyword

import scala.collection._

/**
 * Abstract class extending a KeywordValueActor adding some useful methods
 *
 * Actors reading from different sources can extend this class to simplify building those actors */
abstract class OneItemKeywordValueActor(private val config: GDSConfiguration) extends KeywordValueActor {
  protected val LOG: Logger = Logger.getLogger(this.getClass.getName)

  // Add some inner values to simplify code in the implementations
  protected val fitsKeyword: FitsKeyword = config.keyword
  protected val isMandatory: Boolean = config.isMandatory
  protected val fitsComment: String = config.fitsComment.value
  protected val headerIndex: Int = config.index.index
  protected val sourceChannel: String = config.channel.name
  protected val defaultValue: String = config.nullValue.value
  protected val dataType: DataType = config.dataType
  protected val arrayIndex: Int = config.arrayIndex.value
  protected val format: Option[String] = config.format.value

  override def exceptionHandler = {
    case e: Exception =>
      LOG log (Level.SEVERE, "Unhandled exception while collecting data item", e)
      reply(immutable.List(ErrorCollectedValue(fitsKeyword, CollectionError.GenericError, fitsComment, headerIndex)))
  }

  /**
   * Method to convert a value read from a given source to the type requested in the configuration */
  protected def valueToCollectedValue(value: Any): CollectedValue[_] = dataType match {
    // Anything can be converted to a string
    case DataType("STRING") => CollectedValue(fitsKeyword, value.toString, fitsComment, headerIndex, format)
    // Any number can be converted to a double
    case DataType("DOUBLE") => newDoubleCollectedValue(value)
    // Any number can be converted to a int
    case DataType("INT")    => newIntCollectedValue(value)
    // Any string can be converted to a boolean
    case DataType("BOOLEAN") => newBooleanCollectedValue(value)
    // this should not happen
    case _ => newMismatchError
  }

  private def newDoubleCollectedValue(value: Any):CollectedValue[_] = value match {
    case x: java.lang.Number => CollectedValue(fitsKeyword, x.doubleValue, fitsComment, headerIndex, format)
    case _ => newMismatchError
  }

  private def collectedValueFromInt(value: Int):CollectedValue[_] = CollectedValue(fitsKeyword, value, fitsComment, headerIndex, format)

  private def newIntCollectedValue(value: Any) = value match {
    case x: java.lang.Long =>
      LOG.warning(s"Possible loss of precision converting $x to integer")
      collectedValueFromInt(x.intValue)
    case x: java.lang.Integer => collectedValueFromInt(x.intValue)
    case x: java.lang.Short   => collectedValueFromInt(x.intValue)
    case x: java.lang.Byte    => collectedValueFromInt(x.intValue)
    case _                    => newMismatchError
  }

  private def newBooleanCollectedValue(value: Any) = value match {
    case x: java.lang.Number
      => CollectedValue(fitsKeyword, x.doubleValue() != 0, fitsComment, headerIndex, format)
    case x: String if x.equalsIgnoreCase("false") || x.equalsIgnoreCase("f") || x.equalsIgnoreCase("0") || x.isEmpty
      => CollectedValue(fitsKeyword, false, fitsComment, headerIndex, format)
    case x: String
      => CollectedValue(fitsKeyword, true, fitsComment, headerIndex, format)
    case x: Boolean
      => CollectedValue(fitsKeyword, x, fitsComment, headerIndex, format)
    case _
      => newMismatchError
  }

  private def newMismatchError = ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)
}
