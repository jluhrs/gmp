package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import edu.gemini.aspen.giapi.web.ui.vaadin.selects._

@RunWith(classOf[JUnitRunner])
class SelectsTest extends FunSuite {
  test("Table construction") {
    val table = new Table(caption="caption", width=90 px, height=100 px)
    assertEquals("caption", table.getCaption)
    assertEquals(90, table.getWidth, 0)
    assertEquals(100, table.getHeight, 0)
  }

  test("Table generated column") {
    val table = new Table()

    table.addGeneratedColumn("column1", (t:com.vaadin.ui.Table, itemId:AnyRef, columnId:AnyRef) => {
      "cell"
    })
    assertNotNull(table.getColumnGenerator("column1"))
    table.addGeneratedColumn("column2", (itemId:AnyRef, columnId:AnyRef) => {
      "cell"
    })
    assertNotNull(table.getColumnGenerator("column2"))
  }

  test("Test using a function for a Table'-cell style generator") {
    def generator(itemId: AnyRef, propertyId: AnyRef):String = "style"
    val table = new Table(cellStyleGenerator = generator)
    assertNotNull(table.getCellStyleGenerator)
  }

  test("Building NativeSelect") {
    val ns = new NativeSelect(caption = "Caption")
    assertEquals("Caption", ns.getCaption)
  }

  test("NativeSelect with action") {
    val ns = new NativeSelect(caption = "Caption", action = e => {println("e")})
    ns.setValue("VALUE")
  }

}