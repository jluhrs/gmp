package edu.gemini.aspen.gds.web.ui.keywords

import org.junit.Test
import org.junit.Assert._
import com.vaadin.ui.Window
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api.GDSConfiguration

/**
 * Construction tests
 */
class KeywordsTableModuleTest extends Mockito {
  @Test
  def testBuildPanel = {
    // mock configuration service
    val configService = mock[GDSConfigurationService]
    configService.getConfiguration returns List[GDSConfiguration]()
    val module = new KeywordsTableModule(configService)

    val window = mock[Window]
    assertNotNull(module.statusRow(window))
  }
}