package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword

/**
 * Enumeration of possible errors
 */
object CollectionError extends Enumeration {
    type CollectionError = Value
    val MandatoryRequired = Value("MandatoryRequired")
    val TypeMismatch = Value("TypeMismatch")
    val ItemNotFound = Value("ItemNotFound")
}

/**
 * Message indicating the result of a collection event was as error
 */
class ErrorCollectedValue(keyword: FitsKeyword, val error: CollectionError.CollectionError, comment: String, index: Int) extends CollectedValue[String](keyword, "", comment, index) {
    override val isError = true

    // Can only equal the same type
    override protected def canEqual(other: Any): Boolean = other.isInstanceOf[ErrorCollectedValue]

    override def toString = "ErrorCollectedValue(" + keyword + ", " + error + ", " + comment + ", " + index + ")"

}

/**
 * Companion object used to place implicit conversions
 */
object ErrorCollectedValue {
    def apply(keyword: FitsKeyword, error: CollectionError.CollectionError, comment: String, index: Int) = new ErrorCollectedValue(keyword, error, comment, index)

    def unapply(ev: ErrorCollectedValue) = Option(ev.keyword, ev.error, ev.comment, ev.index)
}
