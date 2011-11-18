package edu.gemini.aspen.giapi.web.ui.vaadin

import org.scalatest.FunSuite
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.vaadin.data.util.ObjectProperty

@RunWith(classOf[JUnitRunner])
class ScalaComponentsTest extends FunSuite {
  test("label component construction") {
    var l = new Label(caption="Caption")
    assertEquals("Caption", l.getCaption)

    l = new Label(style="style")
    assertEquals("style", l.getStyleName)

    l = new Label(content="Content")
    assertEquals("Content", l.getPropertyDataSource.getValue)

    l = new Label(property=new ObjectProperty("Content"))
    assertEquals("Content", l.getPropertyDataSource.getValue)
  }
}