package edu.gemini.aspen.gds.api

import fits.{FitsKeyword, HeaderItem}
import scala.language.implicitConversions

/**
 * Message indicating the resulting values */
class CollectedValue[T] protected(val keyword: FitsKeyword, val value: T, val comment: String, val index: Int, val format: Option[String])(implicit val _type: FitsType[T]) {
  val isError = false

  override def equals(other: Any): Boolean = other match {
    case that: CollectedValue[_] => (that canEqual this) && keyword == that.keyword && value == that.value && comment == that.comment && index == that.index && format.getOrElse("") == that.format.getOrElse("")
    case _ => false
  }

  // Used by equals and can be overrode by extensions
  protected def canEqual(other: Any): Boolean = other.isInstanceOf[CollectedValue[_]]

  override def hashCode: Int = 41 * (41 * (41 * (41 * (41 + index) + comment.##) + value.##) + keyword.##) + format.##

  override def toString = "CollectedValue(" + keyword + ", " + value + ", " + comment + ", " + index + ", " + format.getOrElse("\"\"") + ")"
}

/**
 * Companion object used to place implicit conversions, apply and unapply */
object CollectedValue {
  implicit def collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit _type: FitsType[T]): HeaderItem[T] = {
    _type.collectedValueToHeaderItem(collectedValue)
  }

  def apply[T](keyword: FitsKeyword, value: T, comment: String, index: Int, format: Option[String])(implicit _type: FitsType[T]) = new CollectedValue[T](keyword, value, comment, index, format)

  def unapply(cv: CollectedValue[_]) = Option(cv.keyword, cv.value, cv.comment, cv.index)
}