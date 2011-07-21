package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.gds.api.{GDSEvent, GDSConfiguration}
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.NativeSelect

class GDSEventPropertyFactoryTest {
  val factory = new GDSEventPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("GDSEvent", new ObjectProperty[GDSEvent](GDSEvent("OBS_START_ACQ")))

  @Test
  def testColumnDefinition {
    assertEquals("GDSEvent", factory.title)
    assertEquals(classOf[NativeSelect], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testSelectProperties {
    val (select, _) = factory.buildPropertyControlAndWrapper(config)
    assertFalse(select.isNullSelectionAllowed)
  }

  @Test
  def testBuildAndChange {
    val (select, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    // Simulates that the combo box has been updated
    select.setValue("END_ACQ")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_END_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}
