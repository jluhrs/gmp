package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import com.vaadin.ui.TextField
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.{Ignore, Test}
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.giapi.data.FitsKeyword
import com.vaadin.data.Item

/**
 * Test of the property wrapper
 */
class FitsKeywordPropertyFactoryTest {
  val factory = new FitsKeywordPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem

  @Test
  def testColumnDefinition {
    assertEquals("FitsKeyword", factory.title)
    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    item.addItemProperty("FitsKeyword", new ObjectProperty[FitsKeyword]("KEY"))

    val (_, wrapperFunction) = factory.createItemAndWrapper(config, item)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    item.addItemProperty("FitsKeyword", new ObjectProperty[FitsKeyword]("KEY"))

    val (textField, wrapperFunction) = factory.createItemAndWrapper(config, item)
    // Simulates that the text field has been updated
    textField.setValue("NEWKEY")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "NEWKEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    item.addItemProperty("FitsKeyword", new ObjectProperty[FitsKeyword]("KEY"))

    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}