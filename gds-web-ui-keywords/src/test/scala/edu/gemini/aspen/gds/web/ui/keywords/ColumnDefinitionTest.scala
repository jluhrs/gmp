package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.CheckBox

class ColumnDefinitionTest {
  @Test
  def testMandatoryColumnDefinition {
    val definition = new MandatoryColumnDefinition
    assertEquals("Mandatory", definition.title)
    assertEquals(classOf[CheckBox], definition.columnType)
  }
}