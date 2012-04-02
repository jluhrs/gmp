package edu.gemini.aspen.giapi.web.ui.vaadin

import org.scalatest.FunSuite
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import edu.gemini.aspen.giapi.web.ui.vaadin.containers._

@RunWith(classOf[JUnitRunner])
class ScalaContainersTest extends FunSuite {
  test("tabsheet component construction") {
    val t = new TabSheet(caption = "Caption")
    assertEquals("Caption", t.getCaption)
  }

  test("panel component construction") {
    val t = new Panel(caption = "Caption")
    assertEquals("Caption", t.getCaption)
  }

}