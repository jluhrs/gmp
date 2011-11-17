package edu.gemini.aspen.giapi.web.ui.vaadin

import org.scalatest.FunSuite
import org.junit.Assert._

class ScalaComponentsTest extends FunSuite {
  test("label component") {
    val l = new Label(caption="Caption", style="style")
    assertEquals("Caption", l.getCaption)
    assertEquals("style", l.getStyleName)
  }
}