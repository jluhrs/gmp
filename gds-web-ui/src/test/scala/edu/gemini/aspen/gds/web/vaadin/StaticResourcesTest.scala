package edu.gemini.aspen.gds.web.vaadin

import org.junit.Test
import org.junit.Assert._
import org.specs2.mock.Mockito
import org.osgi.framework.BundleContext

class StaticResourcesTest extends Mockito {
    @Test
    def testConstruction{
        val ctx = mock[BundleContext]

        ctx.getBundles returns Array()

        val staticResources = new StaticResources(ctx)
        assertNotNull(staticResources)
    }
    
    @Test
    def testUnknownResource {
        val ctx = mock[BundleContext]

        ctx.getBundles returns Array()

        val staticResources = new StaticResources(ctx)
        assertNotNull(staticResources)
    }
}