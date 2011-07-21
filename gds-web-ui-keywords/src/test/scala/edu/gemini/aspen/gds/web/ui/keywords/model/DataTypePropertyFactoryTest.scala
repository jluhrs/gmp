package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import com.vaadin.ui.NativeSelect
import edu.gemini.aspen.gds.api.{DataType, GDSConfiguration}

/**
 * Test of the property wrapper
 */
class DataTypePropertyFactoryTest {
  val factory = new DataTypePropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("DataType", new ObjectProperty[DataType](DataType("INT")))

  @Test
  def testColumnDefinition {
    assertEquals("DataType", factory.title)
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
    select.setValue("STRING")

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "STRING", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}