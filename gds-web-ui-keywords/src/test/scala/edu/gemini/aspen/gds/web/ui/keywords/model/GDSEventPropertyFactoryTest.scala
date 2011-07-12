package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import com.vaadin.ui.NativeSelect
import edu.gemini.aspen.gds.api.{GDSEvent, GDSConfiguration}
import edu.gemini.aspen.giapi.data.ObservationEvent

/**
 * Test of the property wrapper
 */
class GDSEventPropertyFactoryTest {
  val factory = new GDSEventPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("GDSEvent", new ObjectProperty[GDSEvent](GDSEvent(ObservationEvent.OBS_START_ACQ.toString)))

  @Test
  def testColumnDefinition {
    assertEquals("GDSEvent", factory.title)
    assertEquals(classOf[NativeSelect], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.createItemAndWrapper(config, item)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testSelectProperties {
    val (select, _) = factory.createItemAndWrapper(config, item)
    assertFalse(select.isNullSelectionAllowed)
  }

  @Test
  def testBuildAndChange {
    val (nativeSelect, wrapperFunction) = factory.createItemAndWrapper(config, item)
    // Simulates that the combo box has been updated
    nativeSelect.setValue("OBS_END_ACQ")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_END_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}