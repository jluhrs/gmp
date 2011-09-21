package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Test
import org.junit.Assert._
import com.vaadin.Application
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.api.configuration.{ConfigItem, GDSConfigurationService}

/**
 * Trivial tests
 */
class KeywordsTableModuleFactoryTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock configuration service
    val configService = mock[GDSConfigurationService]
    configService.getFullConfiguration returns List[ConfigItem[_]]()

    // test building the module
    val module = new KeywordsTableModuleFactory(configService).buildWebModule
    assertNotNull(module)
    val app = mock[Application]
    app.getUser returns None
    assertNotNull(module.buildTabContent(app))
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