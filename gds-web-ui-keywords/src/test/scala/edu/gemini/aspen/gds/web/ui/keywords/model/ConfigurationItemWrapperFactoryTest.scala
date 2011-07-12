package edu.gemini.aspen.gds.web.ui.keywords.model

import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.CheckBox

class ConfigurationItemWrapperFactoryTest {
  @Test
  def testMandatoryColumnDefinition {
    val definition = new MandatoryConfigurationItemWrapperFactory
    assertEquals("Mandatory", definition.title)
    assertEquals(classOf[CheckBox], definition.columnType)
  }
}