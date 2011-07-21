package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.gds.api.{ArrayIndex, GDSConfiguration}
import com.vaadin.ui.TextField

/**
 * Test of the property wrapper
 */
class ArrayIndexPropertyFactoryTest {
  val factory = new ArrayIndexPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem

  @Test
  def testColumnDefinition {
    assertEquals("index", factory.title)
    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    item.addItemProperty("ArrayIndex", new ObjectProperty[ArrayIndex](0))

    val (_, wrapperFunction) = factory.createItemAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    item.addItemProperty("ArrayIndex", new ObjectProperty[ArrayIndex](2))

    val (select, wrapperFunction) = factory.createItemAndWrapper(config)
    // Simulates that the text field has been updated
    select.setValue("2")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 2, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }
}