package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.Assert._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class UnitsTest extends FunSuite {
  test("Percent conversions") {
    assertEquals("100%", 100.percent)
    assertEquals("100%", 100.pct)
  }

  test("Pixel conversions") {
    assertEquals("100px", 100.px)
  }

  test("em conversions") {
    assertEquals("100em", 100.em)
  }

  test("ex conversions") {
    assertEquals("100ex", 100.ex)
  }

  test("inch conversions") {
    assertEquals("100in", 100.in)
  }

  test("cm conversions") {
    assertEquals("100cm", 100.cm)
  }

  test("mm conversions") {
    assertEquals("100mm", 100.mm)
  }

  test("pt conversions") {
    assertEquals("100pt", 100.pt)
  }

  test("pc conversions") {
    assertEquals("100pc", 100.pc)
  }

}