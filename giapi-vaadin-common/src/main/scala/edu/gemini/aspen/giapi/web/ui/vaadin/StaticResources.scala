package edu.gemini.aspen.giapi.web.ui.vaadin

import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.Servlet
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.api.Predef._
import scala.collection.JavaConversions._

/**
 * Servlet that can serve static resources needed by Vaadin
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[Servlet]))
class StaticResources(ctx: BundleContext) extends HttpServlet {
  @ServiceProperty(name = "alias", value = "/VAADIN")
  val label: String = "/VAADIN"
  @ServiceProperty(name = "servlet-name", value = "VaadinResourcesServlet")
  val servletName: String = "VaadinResourcesServlet"
  val vaadinBundle = findVaadinBundle(ctx)
  val widgetBundles = findWidgetBundles(ctx)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val path = req.getPathInfo
    val resourcePath = "VAADIN" + path

    val u = vaadinBundle flatMap {
      b => Option(b.getResource(resourcePath))
    }
    u map {
        ur: URL =>
        use(ur.openStream) {
            in => use(resp.getOutputStream()) {
              out =>
                val buffer = new Array[Byte](1024)
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

  /**
   * Finds the Vaadin Bundle
   */
  private def findVaadinBundle(ctx: BundleContext): Option[Bundle] =
    ctx.getBundles().toList filter {
        v: Bundle => "com.vaadin".equals(v.getSymbolicName())
    } headOption

  /**
   * Finds any Bundle with a widgetset
   */
  private def findWidgetBundles(ctx: BundleContext): List[Bundle] =
    ctx.getBundles().toList collect {
      case b: Bundle => (b, b.getHeaders)
    } filter {
      case (b, h) => h != null && h.toMap.containsKey("Vaadin-Widgetsets")
    } collect {
      case (b, _) => b
    }
}
