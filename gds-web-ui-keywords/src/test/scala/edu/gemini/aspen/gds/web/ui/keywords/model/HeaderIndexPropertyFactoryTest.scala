package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.gds.api.{HeaderIndex, GDSConfiguration}
import com.vaadin.ui.NativeSelect

/**
 * Test of the property wrapper
 */
class HeaderIndexPropertyFactoryTest {
  val factory = new HeaderIndexPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("HeaderIndex", new ObjectProperty[HeaderIndex](HeaderIndex(0)))

  @Test
  def testColumnDefinition {
    assertEquals("Header", factory.title)
    assertEquals(classOf[NativeSelect], factory.columnType)
  }

  @Test
  def testBuildItem {
    item.addItemProperty("HeaderIndex", new ObjectProperty[HeaderIndex](0))

    val (_, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    item.addItemProperty("HeaderIndex", new ObjectProperty[HeaderIndex](2))

    val (select, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    // Simulates that the text field has been updated
    select.setValue("2")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 2, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

}