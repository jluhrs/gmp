package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import edu.gemini.aspen.giapi.web.ui.vaadin.selects._
import java.util.concurrent.atomic.AtomicReference
import edu.gemini.aspen.giapi.web.ui.vaadin.data.Property

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
    val ref = new AtomicReference[String]("")
    val ns = new NativeSelect(caption = "Caption", action = e => {ref.set(e.getProperty.toString)})
    ns.addItem("VALUE")
    ns.setValue("VALUE")

    assertEquals("VALUE", ref.get())
  }

  test("NativeSelect with property") {
    val ref = new AtomicReference[String]("")
    val ns = new NativeSelect(caption = "Caption", property = Property("INFO"), action = e => {ref.set(e.getProperty.toString)})

    assertEquals("INFO", ref.get())
  }

}