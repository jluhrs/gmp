package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import com.vaadin.ui.TextField
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.giapi.data.FitsKeyword
import com.vaadin.data.Validator.InvalidValueException
import edu.gemini.aspen.gds.api.{DefaultValue, GDSConfiguration}

/**
 * Test of the property wrapper
 */
class DefaultValuePropertyFactoryTest {
  val factory = new DefaultValuePropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "NULL", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("DefaultValue", new ObjectProperty[DefaultValue]("NOTNULL"))

  @Test
  def testColumnDefinition {
    assertEquals("DefaultValue", factory.title)
//    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.createItemAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    val (textField, wrapperFunction) = factory.createItemAndWrapper(config)
    // Simulates that the text field has been updated
    textField.setValue("NOTNULL")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "NOTNULL", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}