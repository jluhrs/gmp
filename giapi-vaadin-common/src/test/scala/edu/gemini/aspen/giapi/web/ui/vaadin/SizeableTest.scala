package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.Assert._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SizeableTest extends FunSuite {
  test("Percent conversions") {
    assertEquals("100%", 100 percent)
  }
}