package edu.gemini.aspen.gds.web.ui.configuration

import org.junit.Test
import org.junit.Assert._
import com.vaadin.Application
import org.mockito.Mockito._
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.osgi.service.cm.ConfigurationAdmin
import edu.gemini.aspen.gmp.services.properties.GmpProperties

class ConfigurationModuleTest {
  @Test
  def testBuildPanel() {
    val propertyHolder = mock(classOf[PropertyHolder])
    val configurationAdmin = mock(classOf[ConfigurationAdmin])
    val module = new ConfigurationModule(propertyHolder, configurationAdmin)

    val app = mock(classOf[Application])
    assertNotNull(module.buildTabContent(app))
  }

  @Test
  def testProperties() {
    val propertyHolder = mock(classOf[PropertyHolder])
    when(propertyHolder.getProperty(org.mockito.Matchers.eq(GmpProperties.GMP_HOST_NAME.name()))).thenReturn("val")
    val configurationAdmin = mock(classOf[ConfigurationAdmin])
    val module = new ConfigurationModule(propertyHolder, configurationAdmin)

    assertEquals(module._properties.head.prop.getDefault, module._properties.head.textField.getValue.toString)

    val app = mock(classOf[Application])
    assertNotNull(module.buildTabContent(app))
    module.refresh()

    assertEquals(propertyHolder.getProperty(module._properties.head.prop.name()), module._properties.head.textField.getValue.toString)

  }

}