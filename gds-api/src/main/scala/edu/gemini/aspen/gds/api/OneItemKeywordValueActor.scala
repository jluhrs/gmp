package edu.gemini.aspen.gds.api

/**
 * Abstract class extending a KeywordValueActor adding some useful methods
 *
 * Actors reading from different sources can extend this class to simplify building those actors
 */
abstract class OneItemKeywordValueActor(private val config: GDSConfiguration) extends KeywordValueActor {
    // Add some inner values to simplify code in the implementations
    protected val fitsKeyword = config.keyword
    protected val isMandatory = config.isMandatory
    protected val fitsComment = config.fitsComment.value
    protected val headerIndex = config.index.index
    protected val sourceChannel = config.channel.name
    protected val defaultValue = config.nullValue.value
    protected val dataType = config.dataType
    protected val arrayIndex = config.arrayIndex.value

    /**
     * Method to get the default value of the value or None if the value is mandatory
     *
     * @return An Option containing the default value or None if the Item is mandatory
     */
    protected def defaultCollectedValue: CollectedValue[_] = if (!isMandatory) {
        DefaultCollectedValue(fitsKeyword, defaultValue, fitsComment, headerIndex)
    } else {
        ErrorCollectedValue(fitsKeyword, CollectionError.MandatoryRequired, fitsComment, headerIndex)
    }

    /**
     * Method to convert a value read from a given source to the type requested in the configuration
     *
     */
    protected def valueToCollectedValue(value: AnyRef): CollectedValue[_] = dataType match {
        // Anything can be converted to a string
        case DataType("STRING") => CollectedValue(fitsKeyword, value.toString, fitsComment, headerIndex)
        // Any number can be converted to a double
        case DataType("DOUBLE") => newDoubleCollectedValue(value)
        // Any number can be converted to a int
        case DataType("INT") => newIntCollectedValue(value)
        // this should not happen
        case _ => newMismatchError
    }

    private def newMismatchError = ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)

    private def newIntCollectedValue(value: AnyRef) = value match {
        // todo: Perhaps this is too liberal, it lets anything to be converted to a int
        case x: java.lang.Number => CollectedValue(fitsKeyword, x.intValue, fitsComment, headerIndex)
        case _ => newMismatchError
    }

    private def newDoubleCollectedValue(value: AnyRef) = value match {
        case x: java.lang.Number => CollectedValue(fitsKeyword, x.doubleValue, fitsComment, headerIndex)
        case _ => newMismatchError
    }

}
