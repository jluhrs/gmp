package edu.gemini.aspen.gds.api

import org.junit.Assert._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FitsTypeTest extends FunSuite {

  private def method(a: Int) = a

  private def method(a: Double) = a

  private def method(a: String) = a

  private def method(a: Boolean) = a

  private def fitsMethod[T](fits: T)(implicit _fits: FitsType[T]) = {
    _fits match {
      case FitsType.IntegerType => method(fits.asInstanceOf[Int])
      case FitsType.StringType => method(fits.asInstanceOf[String])
      case FitsType.DoubleType => method(fits.asInstanceOf[Double])
      case FitsType.BooleanType => method(fits.asInstanceOf[Boolean])
    }
  }

  test("Basic types") {
    assertEquals(1, fitsMethod(1))
    assertEquals(1.0, fitsMethod(1.0))
    assertEquals("1", fitsMethod("1"))
    // The next line couldn't compile
    //fitsMethod(new StringBuilder())
  }

  test("boolean type") {
    assertEquals(true, fitsMethod(true))
  }

}