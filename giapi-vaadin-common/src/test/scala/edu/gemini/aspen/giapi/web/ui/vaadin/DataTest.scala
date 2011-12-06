package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import edu.gemini.aspen.giapi.web.ui.vaadin.data._

@RunWith(classOf[JUnitRunner])
class DataTest extends FunSuite {
  test("Property companion") {
    val p = Property("value")
    assertEquals("value", p.getValue)
    assertEquals(classOf[String], p.getType)
  }

  test("Item companion") {
    val empty = Item()
    assertTrue(empty.getItemPropertyIds.isEmpty)

    val i = Item("id" -> 1)
    assertTrue(i.getItemPropertyIds.contains("id"))
  }
}