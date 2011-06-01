package edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test


class FitsTypeTest extends AssertionsForJUnit {

  private def method(a: Int) {
    println("int: " + a)
  }

  private def method(a: Double) {
    println("double: " + a)
  }

  private def method(a: String) {
    println("string: " + a)
  }

  private def fitsMethod[T](fits: T)(implicit _fits: FitsType[T]) {
    _fits match {
      case i if i == FitsType.IntegerType => method(fits.asInstanceOf[Int])
      case i if i == FitsType.StringType => method(fits.asInstanceOf[String])
      case i if i == FitsType.DoubleType => method(fits.asInstanceOf[Double])
    }
  }

  @Test
  def testBasic() {
    fitsMethod(1)
    fitsMethod(1.0)
    fitsMethod("1")
    //todo: this is not a proper test
  }


}