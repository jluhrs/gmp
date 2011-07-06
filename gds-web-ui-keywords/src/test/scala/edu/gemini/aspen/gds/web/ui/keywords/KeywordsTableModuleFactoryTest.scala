package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.Window
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api.GDSConfiguration

/**
 * Trivial tests
 */
class KeywordsTableModuleFactoryTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock configuration service
    val configService = mock[GDSConfigurationService]
    configService.getConfiguration returns List[GDSConfiguration]()

    // test building the module
    val module = new KeywordsTableModuleFactory(configService).buildWebModule
    assertNotNull(module)
    assertNotNull(module.buildTabContent(new Window()))
  }

  @Test
  def testEqualityAndHash = {
    val configService = mock[GDSConfigurationService]

    val module1 = new KeywordsTableModuleFactory(configService)
    val module2 = new KeywordsTableModuleFactory(configService)
    assertEquals(module1, module2)
    assertEquals(module1.##, module2.##)
  }
}