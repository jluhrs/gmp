package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import com.vaadin.ui.{Label, TextField}
import edu.gemini.aspen.gds.api.{Instrument, ArrayIndex, GDSConfiguration}

/**
 * Test of the property wrapper
 */
class InstrumentPropertyFactoryTest {
  val factory = new InstrumentPropertyFactory
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
  val item = new PropertysetItem

  @Test
  def testColumnDefinition {
    assertEquals("Instrument", factory.title)
    assertEquals(classOf[Label], factory.columnType)
  }

  @Test
  def testBuildItem {
    item.addItemProperty("Instrument", new ObjectProperty[Instrument]("GPI"))

    val (_, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    item.addItemProperty("ArrayIndex", new ObjectProperty[ArrayIndex](2))

    val (select, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    // Simulates that the text field has been updated
    select.setValue("GEMS")

    // Cannot change the instrument
    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }
}