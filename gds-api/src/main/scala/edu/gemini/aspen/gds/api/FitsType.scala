package edu.gemini.aspen.gds.api

abstract class FitsType[T]

object FitsType {

  implicit object IntegerType extends FitsType[Int]

  implicit object StringType extends FitsType[String]

  implicit object DoubleType extends FitsType[Double]

}