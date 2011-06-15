package edu.gemini.aspen.gds.api

/**
 * Abstract class extending a KeywordValueActor adding some useful methods
 */
abstract class OneItemKeywordValueActor(private val config:GDSConfiguration) extends KeywordValueActor {
    // Add some inner values to simplify code in the implementations
    protected val fitsKeyword = config.keyword
    protected val isMandatory = config.isMandatory
    protected val fitsComment = config.fitsComment.value
    protected val headerIndex = config.index.index
    protected val sourceChannel = config.channel.name
    protected val defaultValue = config.nullValue.value

    /**
     * Method to get the default value of the value or None if the value is mandatory
     *
     * @return An Option containing the default value or None if the Item is mandatory
     */
    protected def defaultCollectedValue(): Option[CollectedValue[_]] = if (!isMandatory) {
        Option(CollectedValue(fitsKeyword, defaultValue, fitsComment, headerIndex))
    } else {
        None
    }

}
