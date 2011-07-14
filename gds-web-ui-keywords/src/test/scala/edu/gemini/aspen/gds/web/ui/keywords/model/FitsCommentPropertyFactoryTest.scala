package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Assert._
import com.vaadin.ui.TextField
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}
import com.vaadin.data.Validator.InvalidValueException
import edu.gemini.aspen.gds.api.{FitsComment, GDSConfiguration}

/**
 * Test of the property wrapper
 */
class FitsCommentPropertyFactoryTest {
  val factory = new FitsCommentPropertyFactory
  val fitsComment = "my comment"
  val config = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, fitsComment)
  val item = new PropertysetItem
  item.addItemProperty("FitsComment", new ObjectProperty[FitsComment](fitsComment))

  @Test
  def testColumnDefinition {
    assertEquals("FitsComment", factory.title)
    assertEquals(classOf[TextField], factory.columnType)
  }

  @Test
  def testBuildItem {
    val (_, wrapperFunction) = factory.createItemAndWrapper(config, item)
    assertEquals(config, wrapperFunction(config))
  }

  @Test
  def testBuildAndChange {
    val (textField, wrapperFunction) = factory.createItemAndWrapper(config, item)
    // Simulates that the text field has been updated
    val newComment: String = "another comment"
    textField.setValue(newComment)

    val updatedConfig = new GDSConfiguration("GPI", "OBS_START_EVENT", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "another comment")
    assertEquals(updatedConfig, wrapperFunction(config))
  }

  @Test
  def testPopulateItem {
    val wrapperFunction = factory.populateItem(config, item)
    assertEquals(config, wrapperFunction(config))
  }
}