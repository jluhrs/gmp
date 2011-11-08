package edu.gemini.aspen.gpi.web.ui.aoplot

import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext
import org.apache.felix.ipojo.annotations._
import javax.servlet.Servlet
import edu.gemini.aspen.gds.api.Predef._

/**
 * Servlet that can serve static resources needed by Vaadin
 *
 * TODO Replace with a pax-extender resource bundle
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[Servlet]))
class JSResources(ctx: BundleContext) extends HttpServlet {
  @ServiceProperty(name = "alias", value = "/gpi/js")
  val label: String = "/gpi/js"
  @ServiceProperty(name = "servlet-name", value = "JSAOResourcesServlet")
  val servletName: String = "JSAOResourcesServlet"
  val localBundle = findLocalBundle(ctx)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val path = "js" + req.getPathInfo

    val u:Option[URL] = localBundle flatMap {
        b => Option(b.getResource(path))
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

  def findLocalBundle(ctx: BundleContext):Option[Bundle] = Option(ctx.getBundle)
}
