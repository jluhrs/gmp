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
import javax.servlet.ServletOutputStream
import java.util.{Dictionary, Hashtable}

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

  test("known resource") {
    val ctx = mock[BundleContext]
    val vaadinBundle = mock[Bundle]
    val request = mock[HttpServletRequest]
    val response = mock[HttpServletResponse]
    val sos = mock[ServletOutputStream]

    when(ctx.getBundles) thenReturn Array(vaadinBundle)
    when(vaadinBundle.getSymbolicName) thenReturn "com.vaadin"
    val resource = "/file.css"
    val url = classOf[StaticResourcesTest].getResource("VAADIN" + resource)
    when(vaadinBundle.getResource("VAADIN" + resource)) thenReturn url

    val staticResources = new StaticResources(ctx)
    when(request.getPathInfo) thenReturn resource
    when(response.getOutputStream) thenReturn sos
    staticResources.doGet(request, response)

    verify(response).getOutputStream
  }

  test("resource in a widgetset") {
    val ctx = mock[BundleContext]
    val vaadinBundle = mock[Bundle]
    val request = mock[HttpServletRequest]
    val response = mock[HttpServletResponse]
    val sos = mock[ServletOutputStream]

    when(ctx.getBundles) thenReturn Array(vaadinBundle)
    val h = new Hashtable[String, String]()
    h.put("Vaadin-Widgetsets", "mywidgetset")
    when(vaadinBundle.getHeaders).thenAnswer(new Answer[Dictionary[_, _]]() {
      def answer(p1: InvocationOnMock) = h
    })
    val resource = "/file.css"
    val url = classOf[StaticResourcesTest].getResource("VAADIN" + resource)
    when(vaadinBundle.getResource("VAADIN" + resource)) thenReturn url

    val staticResources = new StaticResources(ctx)
    when(request.getPathInfo) thenReturn resource
    when(response.getOutputStream) thenReturn sos
    staticResources.doGet(request, response)

    verify(response).getOutputStream
  }
}