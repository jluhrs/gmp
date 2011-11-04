package edu.gemini.aspen.gds.api

import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}
import scala.collection._

/**
 * Defines a type class for Fits keywords */
abstract class FitsType[T] {
  def collectedValueToHeaderItem(collectedValue: CollectedValue[T]): HeaderItem
}

/**
 * Implicit conversions that limit what values can be passed as Fits Keywords */
object FitsType {

  object TypeNames extends Enumeration {
    val STRING, DOUBLE, INT = Value
  }

  implicit object IntegerType extends FitsType[Int] {

    override def collectedValueToHeaderItem(collectedValue: CollectedValue[Int]): HeaderItem = {
      DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value, collectedValue.comment)
    }
  }

  implicit object StringType extends FitsType[String] {
    override def collectedValueToHeaderItem(collectedValue: CollectedValue[String]): HeaderItem = {
      DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value, collectedValue.comment)
    }
  }

  implicit object DoubleType extends FitsType[Double] {
    override def collectedValueToHeaderItem(collectedValue: CollectedValue[Double]): HeaderItem = {
      DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value, collectedValue.comment)
    }
  }

}