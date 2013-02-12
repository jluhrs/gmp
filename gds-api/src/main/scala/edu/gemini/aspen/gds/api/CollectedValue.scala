package edu.gemini.aspen.gds.api

import fits.{FitsKeyword, HeaderItem}

/**
 * Message indicating the resulting values */
class CollectedValue[T] protected(val keyword: FitsKeyword, val value: T, val comment: String, val index: Int, val format: Option[String])(implicit val _type: FitsType[T]) {
  val isError = false

  override def equals(other: Any): Boolean = other match {
    case that: CollectedValue[_] => (that canEqual this) && keyword == that.keyword && value == that.value && comment == that.comment && index == that.index && format.getOrElse("") == that.format.getOrElse("")
    case _                       => false
  }

  // Used by equals and can be overrode by extensions
  protected def canEqual(other: Any): Boolean = other match {
    case _:CollectedValue[_] => true
    case _                   => false
  }

  override def hashCode: Int = 41 * (41 * (41 * (41 * (41 + index) + comment.##) + value.##) + keyword.##) + format.##

  override def toString = s"CollectedValue(${keyword}, ${value}, ${comment}, ${index}, ${format.getOrElse("\"\"")})"
}

/**
 * Companion object used to place implicit conversions, apply and unapply */
object CollectedValue {
  implicit class collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit _type: FitsType[T]) extends HeaderItem[T](collectedValue.keyword, collectedValue.value, collectedValue.comment, collectedValue.format) {
    _type.collectedValueToHeaderItem(collectedValue)
  }

  def apply[T](keyword: FitsKeyword, value: T, comment: String, index: Int, format: Option[String])(implicit _type: FitsType[T]) = new CollectedValue[T](keyword, value, comment, index, format)

  def unapply[T](cv: CollectedValue[T]) = Option(cv.keyword, cv.value, cv.comment, cv.index)
}