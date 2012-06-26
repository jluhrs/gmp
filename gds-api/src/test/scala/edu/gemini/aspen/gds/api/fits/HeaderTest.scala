package edu.gemini.aspen.gds.api.fits

import org.junit.Assert._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import edu.gemini.aspen.gds.api.Conversions._

@RunWith(classOf[JUnitRunner])
class HeaderTest extends FunSuite {
  test("contains key") {
    val h = Header(0, List(HeaderItem("KEY", "value", "comment", None)))
    assertTrue(h.containsKey("KEY"))
    assertFalse(h.containsKey("KEY2"))
  }
}