package edu.gemini.aspen.gds.web.ui.configuration

import org.junit.Test
import org.junit.Assert._
import com.vaadin.Application
import org.specs2.mock.Mockito
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.osgi.service.cm.ConfigurationAdmin

/**
 * Trivial tests
 */
class ConfigurationModuleFactoryTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock configuration service
    val propertyHolder = mock[PropertyHolder]
    val configurationAdmin = mock[ConfigurationAdmin]

    // test building the module
    val module = new ConfigurationModuleFactory(propertyHolder, configurationAdmin).buildWebModule
    assertNotNull(module)
    val app = mock[Application]
    app.getUser returns None
    assertNotNull(module.buildTabContent(app))
  }

  @Test
  def testEqualityAndHash = {
    val propertyHolder = mock[PropertyHolder]
    val configurationAdmin = mock[ConfigurationAdmin]

    val module1 = new ConfigurationModuleFactory(propertyHolder, configurationAdmin)
    val module2 = new ConfigurationModuleFactory(propertyHolder, configurationAdmin)
    assertEquals(module1, module2)
    assertEquals(module1.##, module2.##)
  }
}