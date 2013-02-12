package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Label
import com.vaadin.ui.Alignment
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.{HorizontalLayout, VerticalLayout}

@RunWith(classOf[JUnitRunner])
class LayoutsTest extends FunSuite {
  test("Vertical Layout construction") {
    var layout = new VerticalLayout(width = 90.percent, height = 90.percent)
    assertEquals(90.0, layout.getWidth, 0)
    assertEquals(90.0, layout.getHeight, 0)

    layout = new VerticalLayout(sizeFull = true)
    assertEquals(100.0, layout.getWidth, 0)
    assertEquals(100.0, layout.getHeight, 0)

    layout = new VerticalLayout(margin = true, spacing = true, caption = "Caption", style = "style")
    assertTrue(layout.getMargin.hasBottom)
    assertTrue(layout.getMargin.hasTop)
    assertTrue(layout.getMargin.hasLeft)
    assertTrue(layout.getMargin.hasRight)
    assertTrue(layout.isSpacing)
    assertEquals("Caption", layout.getCaption)
    assertEquals("style", layout.getStyleName)
  }

  test("Vertical Layout add component") {
    val l1 = new Label()
    val l2 = new Label()
    val layout = new VerticalLayout {
      add(l1, ratio = 0.5f, alignment = Alignment.BOTTOM_CENTER)
      add(l2, index = 1)
    }
    assertEquals(2, layout.getComponentCount)
    assertEquals(l1, layout.getComponent(0))
    assertEquals(l2, layout.getComponent(1))
  }

  test("HorizontalLayout construction") {
    var layout = new HorizontalLayout(width = 90.percent, height = 90.percent)
    assertEquals(90.0, layout.getWidth, 0)
    assertEquals(90.0, layout.getHeight, 0)

    layout = new HorizontalLayout(sizeFull = true)
    assertEquals(-1.0, layout.getWidth, 0)
    assertEquals(-1.0, layout.getHeight, 0)

    layout = new HorizontalLayout(margin = true, spacing = true, caption = "Caption", style = "style")
    assertTrue(layout.getMargin.hasBottom)
    assertTrue(layout.getMargin.hasTop)
    assertTrue(layout.getMargin.hasLeft)
    assertTrue(layout.getMargin.hasRight)
    assertTrue(layout.isSpacing)
    assertEquals("Caption", layout.getCaption)
    assertEquals("style", layout.getStyleName)
  }

  test("HorizontalLayout add component") {
    val l1 = new Label()
    val l2 = new Label()
    val layout = new HorizontalLayout {
      add(l1, ratio = 0.5f, alignment = Alignment.BOTTOM_CENTER)
      add(l2, index = 1)
    }
    assertEquals(2, layout.getComponentCount)
    assertEquals(l1, layout.getComponent(0))
    assertEquals(l2, layout.getComponent(1))
  }
}