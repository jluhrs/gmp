package edu.gemini.aspen.gds.constant

import edu.gemini.aspen.gds.api._

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class ConstantActor(configurations: List[GDSConfiguration]) extends KeywordValueActor {
    override def collectValues(): List[CollectedValue[_]] = {
        for {config <- configurations} yield getCollectedValue(config: GDSConfiguration)
    }

    /**
     * Method to convert a value read from a given source to the type requested in the configuration
     *
     */
    private def getCollectedValue(config: GDSConfiguration): CollectedValue[_] = {
        try {
            config.dataType match {
                // Anything can be converted to a string
                case DataType("STRING") => CollectedValue(config.keyword, config.nullValue.value, config.fitsComment.value, config.index.index)
                // Any number can be converted to a double
                case DataType("DOUBLE") => CollectedValue(config.keyword, config.nullValue.value.toDouble, config.fitsComment.value, config.index.index)
                // Any number can be converted to a int
                case DataType("INT") => CollectedValue(config.keyword, config.nullValue.value.toInt, config.fitsComment.value, config.index.index)
                // this should not happen
                case _ => ErrorCollectedValue(config.keyword, CollectionError.TypeMismatch, config.fitsComment.value, config.index.index)
            }
        } catch {
            //The string was not parseable as a number
            case ex: NumberFormatException => ErrorCollectedValue(config.keyword, CollectionError.TypeMismatch, config.fitsComment.value, config.index.index)
            case _ => ErrorCollectedValue(config.keyword, CollectionError.GenericError, config.fitsComment.value, config.index.index)
        }
    }
}
