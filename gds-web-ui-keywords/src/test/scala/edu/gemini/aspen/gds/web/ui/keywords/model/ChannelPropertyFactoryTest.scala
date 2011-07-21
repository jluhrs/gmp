package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import com.vaadin.ui.TextField
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.api.Channel
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import com.vaadin.data.Validator.InvalidValueException

/**
 * Test of the property wrapper
 */
class ChannelPropertyFactoryTest {
  val factory = new ChannelPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "a:b", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("Channel", new ObjectProperty[Channel]("a:b"))

  @Test
  def testColumnDefinition {
    assertEquals("Channel", factory.title)
    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    val (textField, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    // Simulates that the text field has been updated
    textField.setValue("c:d")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "c:d", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}