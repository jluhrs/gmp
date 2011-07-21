package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import com.vaadin.ui.NativeSelect
import edu.gemini.aspen.giapi.data.ObservationEvent
import edu.gemini.aspen.gds.api.{KeywordSource, Subsystem, GDSConfiguration}

/**
 * Test of the property wrapper
 */
class SubsystemPropertyFactoryTest {
  val factory = new SubsystemPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "EPICS", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("Subsystem", new ObjectProperty[Subsystem](Subsystem(KeywordSource.EPICS)))

  @Test
  def testColumnDefinition {
    assertEquals("Subsystem", factory.title)
    assertEquals(classOf[NativeSelect], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.createItemAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testSelectProperties {
    val (select, _) = factory.createItemAndWrapper(config)
    assertFalse(select.isNullSelectionAllowed)
  }

  @Test
  def testBuildAndChange {
    val (nativeSelect, wrapperFunction) = factory.createItemAndWrapper(config)
    // Simulates that the combo box has been updated
    nativeSelect.setValue("SEQEXEC")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

}