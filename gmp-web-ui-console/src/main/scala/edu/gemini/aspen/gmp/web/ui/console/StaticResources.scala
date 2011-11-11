package edu.gemini.aspen.gds.web.ui.console

import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext
import org.apache.felix.ipojo.annotations._
import javax.servlet.Servlet
import edu.gemini.aspen.giapi.web.ui.vaadin.Preamble._
import edu.gemini.aspen.gds.api.Predef._

/**
 * Servlet that can serve static resources needed by Vaadin
 *
 * TODO Replace with a pax-extender resource bundle
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[Servlet]))
class StaticResources(ctx: BundleContext) extends HttpServlet {
  @ServiceProperty(name = "alias", value = "/VAADIN")
  val label: String = "/VAADIN"
  @ServiceProperty(name = "servlet-name", value = "VaadinResourcesServlet")
  val servletName: String = "VaadinResourcesServlet"
  val vaadinBundle = findVaadinBundle(ctx)
  val localBundle = findLocalBundle(ctx)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val path = req.getPathInfo
    val resourcePath = "VAADIN" + path

    val u:Option[URL] = vaadinBundle flatMap {
      b => Option(b.getResource(resourcePath))
    } orElse {
      // Find locally
      localBundle flatMap {
        b => Option(b.getResource(resourcePath))
      }
    }
    u map {
        ur: URL =>
        use(ur.openStream) {
            in => use(resp.getOutputStream()) {
              out => val buffer = new Array[Byte](1024)
            Iterator.continually(in.read(buffer))
              .takeWhile(_ != -1)
              .foreach {
              out.write(buffer, 0, _)
            }
          }
        }
    } getOrElse {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND)
    }
  }

  def findVaadinBundle(ctx: BundleContext):Option[Bundle] = ctx.getBundles().toList filter {
      v: Bundle => "com.vaadin".equals(v.getSymbolicName())
  } headOption

  def findLocalBundle(ctx: BundleContext):Option[Bundle] = Option(ctx.getBundle)
}
