package edu.gemini.aspen.gmp.web.ui.properties

import org.specs2.mock.Mockito
import org.junit.Test
import org.junit.Assert.assertNotNull
import com.vaadin.Application
import edu.gemini.aspen.gmp.services.PropertyHolder

class GMPPropertiesModuleFactoryTest extends Mockito {
    @Test
    def testBuildPanel {
        val propertyHolder = mock[PropertyHolder]

        val module = new GMPPropertiesModuleFactory(propertyHolder).buildWebModule
        assertNotNull(module)
        val app = mock[Application]
        assertNotNull(module.buildTabContent(app))
    }
}