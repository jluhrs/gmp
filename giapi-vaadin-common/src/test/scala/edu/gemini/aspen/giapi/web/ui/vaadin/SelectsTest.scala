package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import edu.gemini.aspen.giapi.web.ui.vaadin.selects._

@RunWith(classOf[JUnitRunner])
class SelectsTest extends FunSuite {
  test("Table construction") {
    var table = new Table(caption="caption", width=90 px, height=100 px)
    assertEquals("caption", table.getCaption)
    assertEquals(90, table.getWidth, 0)
    assertEquals(100, table.getHeight, 0)
  }
}