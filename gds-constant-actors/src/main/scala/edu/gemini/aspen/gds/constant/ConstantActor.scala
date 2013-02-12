package edu.gemini.aspen.gds.constant

import edu.gemini.aspen.gds.api._
import scala.collection._

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword */
class ConstantActor(configurations: immutable.List[GDSConfiguration]) extends KeywordValueActor {
  override def collectValues(): immutable.List[CollectedValue[_]] = {
    for {config <- configurations} yield getCollectedValue(config: GDSConfiguration)
  }

  /**
   * Method to convert a value read from a given source to the type requested in the configuration */
  private def getCollectedValue(config: GDSConfiguration): CollectedValue[_] = {
    try {
      config.dataType match {
        // Anything can be converted to a string but we remove start and end quotes
        case DataType("STRING") => CollectedValue(config.keyword, config.nullValue.value.replaceAll("""(^')|(^")|('$)|("$)""",""), config.fitsComment.value, config.index.index, config.format.value)
        // Any number can be converted to a double
        case DataType("DOUBLE") => CollectedValue(config.keyword, config.nullValue.value.toDouble, config.fitsComment.value, config.index.index, config.format.value)
        // Any number can be converted to a int
        case DataType("INT") => CollectedValue(config.keyword, config.nullValue.value.toInt, config.fitsComment.value, config.index.index, config.format.value)
        // Boolean constants need a bit of parsing
        case DataType("BOOLEAN") => CollectedValue(config.keyword, matchBooleanValue(config.nullValue.value), config.fitsComment.value, config.index.index, config.format.value)
        // this should not happen
        case _ => ErrorCollectedValue(config.keyword, CollectionError.TypeMismatch, config.fitsComment.value, config.index.index)
      }
    } catch {
      //The string was not parseable as a number
      case ex: NumberFormatException => ErrorCollectedValue(config.keyword, CollectionError.TypeMismatch, config.fitsComment.value, config.index.index)
      case _:Exception => ErrorCollectedValue(config.keyword, CollectionError.GenericError, config.fitsComment.value, config.index.index)
    }
  }

  private def matchBooleanValue(value: String) = value match {
    case "T" => true
    case "t" => true
    case "true" => true
    case "1" => true
    case _ => false
  }
}
