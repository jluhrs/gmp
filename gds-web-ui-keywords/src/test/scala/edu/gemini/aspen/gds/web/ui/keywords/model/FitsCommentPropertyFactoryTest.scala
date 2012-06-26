package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import com.vaadin.ui.TextField
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import edu.gemini.aspen.gds.api.{FitsComment, GDSConfiguration}

/**
 * Test of the property wrapper
 */
class FitsCommentPropertyFactoryTest {
  val factory = new FitsCommentPropertyFactory
  val fitsComment = "my comment"
  val config = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "", fitsComment)
  val item = new PropertysetItem
  item.addItemProperty("Comment", new ObjectProperty[FitsComment](fitsComment))

  @Test
  def testColumnDefinition {
    assertEquals("Comment", factory.title)
    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    val (textField, wrapperFunction) = factory.buildPropertyControlAndWrapper(config)
    // Simulates that the text field has been updated
    val newComment: String = "another comment"
    textField.setValue(newComment)

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "", "another comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }
}