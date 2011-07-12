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
  @Test
  def testColumnDefinition {
    val factory = new FitsKeywordPropertyFactory
    assertEquals("FitsKeyword", factory.title)
    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "my comment")
    val item = new PropertysetItem
    item.addItemProperty("FitsKeyword", new ObjectProperty[FitsKeyword]("KEY"))

    val factory = new FitsKeywordPropertyFactory
    val function = factory.createItemAndWrapper(config, item)
    assertEquals(config, function(config))
  }
}