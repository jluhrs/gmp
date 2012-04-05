package edu.gemini.aspen.giapi.web.ui.vaadin

import org.scalatest.FunSuite
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import com.vaadin.data.util.ObjectProperty
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import com.vaadin.ui.themes.BaseTheme
import com.vaadin.terminal.ThemeResource

@RunWith(classOf[JUnitRunner])
class ScalaComponentsTest extends FunSuite {
  test("label component construction") {
    var l = new Label(caption = "Caption")
    assertEquals("Caption", l.getCaption)

    l = new Label(style = "style")
    assertEquals("style", l.getStyleName)

    l = new Label(content = "Content")
    assertEquals("Content", l.getPropertyDataSource.getValue)

    l = new Label(property = new ObjectProperty("Content"))
    assertEquals("Content", l.getPropertyDataSource.getValue)
  }

  test("button component construction") {
    var b = new Button(caption = "Caption")
    assertEquals("Caption", b.getCaption)

    b = new Button(style = "style")
    assertEquals("style", b.getStyleName)

    b = new Button(enabled = false)
    assertFalse(b.isEnabled)

    val icon = new ThemeResource("a.png")
    b = new Button(icon = icon)
    assertEquals(icon, b.getIcon)
  }

  test("button component action") {
    new Button(action = _ => {
    })
  }

  test("link button action") {
    val lb = new LinkButton()
    assertEquals(BaseTheme.BUTTON_LINK, lb.getStyleName)
  }

  test("embedded component construction") {
    var e = new Embedded("caption")
    assertEquals("caption", e.getCaption)

    e = new Embedded(width = 10 px, height = 20 px)
    assertEquals(10.0, e.getWidth, 0)
    assertEquals(20.0, e.getHeight, 0)
  }

  test("text field component construction") {
    var t = new TextField("caption")
    assertEquals("caption", t.getCaption)

    t = new TextField(value = "value")
    assertEquals("value", t.getValue)
  }

  test("progress indicator construction") {
    val p = new ProgressIndicator(value = 1, caption = "Caption")
    assertEquals("Caption", p.getCaption)
  }
}