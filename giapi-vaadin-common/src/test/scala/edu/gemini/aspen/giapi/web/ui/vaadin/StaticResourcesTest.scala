package edu.gemini.aspen.giapi.web.ui.vaadin

import org.junit.Assert._
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.osgi.framework.{Bundle, BundleContext}
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.Mockito.verify
import org.junit.runner.RunWith
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

@RunWith(classOf[JUnitRunner])
class StaticResourcesTest extends FunSuite with MockitoSugar {
  test("construction") {
    val ctx = mock[BundleContext]

    when(ctx.getBundles) thenAnswer new Answer[Array[Bundle]]() {
      def answer(invocation: InvocationOnMock) = Array.empty
    }
    
    val staticResources = new StaticResources(ctx)
    assertNotNull(staticResources)
  }

  test("unknown resource") {
    val ctx = mock[BundleContext]
    val vaadinBundle = mock[Bundle]
    val request = mock[HttpServletRequest]
    val response = mock[HttpServletResponse]

    when(ctx.getBundles) thenReturn Array(vaadinBundle)

    val staticResources = new StaticResources(ctx)
    staticResources.doGet(request, response)

    verify(response).sendError(HttpServletResponse.SC_NOT_FOUND)
  }
}