package edu.gemini.aspen.gmp.web.ui.properties

import org.specs2.mock.Mockito
import org.junit.Assert._
import com.vaadin.Application
import org.junit.Test
import edu.gemini.aspen.gmp.services.PropertyHolder

class GMPPropertiesModuleTest extends Mockito {
  @Test
  def testBuildPanel {
    val propertyHolder = mock[PropertyHolder]

    // mock configuration service
    val module = new GMPPropertiesModule(propertyHolder)

    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }
}