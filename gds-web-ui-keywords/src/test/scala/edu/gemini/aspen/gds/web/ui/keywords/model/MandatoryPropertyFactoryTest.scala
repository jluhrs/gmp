package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.gds.api.{Mandatory, GDSConfiguration}
import com.vaadin.ui.{CheckBox, NativeSelect}

/**
 * Test of the property wrapper
 */
class MandatoryPropertyFactoryTest {
  val factory = new MandatoryPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem
  item.addItemProperty("Mandatory", new ObjectProperty[Mandatory](Mandatory(true)))

  @Test
  def testColumnDefinition {
    assertEquals("Mandatory", factory.title)
    assertEquals(classOf[CheckBox], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.createItemAndWrapper(config, item)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testCheckBoxProperties {
    val (checkBox, _) = factory.createItemAndWrapper(config, item)
    assertNotNull(checkBox)
  }

  @Test
  def testBuildAndChange {
    val (nativeSelect, wrapperFunction) = factory.createItemAndWrapper(config, item)
    // Simulates that the combo box has been updated
    nativeSelect.setValue(false)

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", false, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

}