package edu.gemini.aspen.gds.api

import fits.HeaderItem

/**
 * Defines a type class for Fits keywords */
abstract class FitsType[T] {
  def collectedValueToHeaderItem(collectedValue: CollectedValue[T]): HeaderItem[T]
}

/**
 * Implicit conversions that limit what values can be passed as Fits Keywords */
object FitsType {

  object TypeNames extends Enumeration {
    val STRING, DOUBLE, INT, BOOLEAN = Value
  }

  implicit object IntegerType extends FitsType[Int] {

    override def collectedValueToHeaderItem(collectedValue: CollectedValue[Int]): HeaderItem[Int] = {
      HeaderItem(collectedValue.keyword, collectedValue.value, collectedValue.comment, collectedValue.format)
    }
  }

  implicit object StringType extends FitsType[String] {
    override def collectedValueToHeaderItem(collectedValue: CollectedValue[String]): HeaderItem[String] = {
      HeaderItem(collectedValue.keyword, collectedValue.value, collectedValue.comment, collectedValue.format)
    }
  }

  implicit object DoubleType extends FitsType[Double] {
    override def collectedValueToHeaderItem(collectedValue: CollectedValue[Double]): HeaderItem[Double] = {
      HeaderItem(collectedValue.keyword, collectedValue.value, collectedValue.comment, collectedValue.format)
    }
  }

  implicit object BooleanType extends FitsType[Boolean] {
    override def collectedValueToHeaderItem(collectedValue: CollectedValue[Boolean]): HeaderItem[Boolean] = {
      HeaderItem(collectedValue.keyword, collectedValue.value, collectedValue.comment, collectedValue.format)
    }
  }

}