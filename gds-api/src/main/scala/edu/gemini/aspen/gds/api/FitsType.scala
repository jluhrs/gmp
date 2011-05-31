package edu.gemini.aspen.gds.api

abstract class FitsType[T] {
  def getType: Class[_]
}

object FitsType {

  implicit object IntegerType extends FitsType[Int] {
    def getType = classOf[Int]
  }

  implicit object StringType extends FitsType[String] {
    def getType = classOf[String]
  }

  implicit object DoubleType extends FitsType[Double] {
    def getType = classOf[Double]
  }

}