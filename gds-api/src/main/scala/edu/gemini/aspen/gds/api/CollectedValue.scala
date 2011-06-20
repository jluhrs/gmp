package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.HeaderItem

/**
 * Message indicating the resulting values
 */
class CollectedValue[T] protected(val keyword: FitsKeyword, val value: T, val comment: String, val index: Int)(implicit val _type: FitsType[T]) {
    val isError = false

    override def equals(other: Any): Boolean = other match {
        case that: CollectedValue[_] => (that canEqual this) && keyword == that.keyword && value == that.value && comment == that.comment && index == that.index
        case _ => false
    }

    // Used by equals and can be overrode by extensions
    protected def canEqual(other: Any): Boolean = other.isInstanceOf[CollectedValue[_]]

    override def hashCode: Int = 41 * (41 * (41 * (41 + index) + comment.##) + value.##) + keyword.##

    override def toString = "CollectedValue(" + keyword + ", " + value + ", " + comment + ", " + index + ")"
}

/**
 * Companion object used to place implicit conversions, apply and unapply
 */
object CollectedValue {
    implicit def collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit _type: FitsType[T]): HeaderItem = {
        _type.collectedValueToHeaderItem(collectedValue)
    }

    def apply[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(implicit _type: FitsType[T]) = new CollectedValue[T](keyword, value, comment, index)

    def unapply(cv: CollectedValue[_]) = Option(cv.keyword, cv.value, cv.comment, cv.index)
}

/**
 * Message indicating the value used was the default
 */
class DefaultCollectedValue[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(override implicit val _type: FitsType[T]) extends CollectedValue[T](keyword, value, comment, index) {
    // Can only equal the same type
    override protected def canEqual(other: Any): Boolean = other.isInstanceOf[DefaultCollectedValue[_]]

    override def toString = "DefaultCollectedValue(" + keyword + ", " + value + ", " + comment + ", " + index + ")"
}

/**
 * Companion object used to place apply and unapply
 */
object DefaultCollectedValue {
    def apply[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(implicit _type: FitsType[T]) = new DefaultCollectedValue[T](keyword, value, comment, index)

    def unapply(cv: DefaultCollectedValue[_]) = Option(cv.keyword, cv.value, cv.comment, cv.index)
}

object CollectionError extends Enumeration {
    type CollectionError = Value
    val MandatoryRequired = Value("MandatoryRequired")
    val TypeMismatch = Value("TypeMismatch")
    val ItemNotFound = Value("ItemNotFound")
}

/**
 * Message indicating the value used was the default
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
