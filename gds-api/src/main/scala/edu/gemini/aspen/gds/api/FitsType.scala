package edu.gemini.aspen.gds.api

import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}

abstract class FitsType[T]{


     def collectedValueToHeaderItem(collectedValue: CollectedValue[T]): HeaderItem
}

object FitsType {
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


//  def convert(v:T)(f: T = Any) {
//    _type match {
//      case FitsType.IntegerType => DefaultHeaderItem.create(collectedValue.keyword.getName, v.value.asInstanceOf[Int], collectedValue.comment)
//      case FitsType.DoubleType => DefaultHeaderItem.create(collectedValue.keyword.getName, v.asInstanceOf[Double], collectedValue.comment)
//      case FitsType.StringType => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[String], collectedValue.comment)
//    }
//  }
}