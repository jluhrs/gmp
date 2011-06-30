package edu.gemini.aspen.gds.web.vaadin

import org.junit.Test
import org.junit.Assert._
import org.specs2.mock.Mockito
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.osgi.framework.{Bundle, BundleContext}

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
        val vaadinBundle = mock[Bundle]
        val request = mock[HttpServletRequest]
        val response = mock[HttpServletResponse]

        ctx.getBundles returns Array(vaadinBundle)

        val staticResources = new StaticResources(ctx)
        staticResources.doGet(request, response)

        there was one(response).sendError(HttpServletResponse.SC_NOT_FOUND)
    }
}