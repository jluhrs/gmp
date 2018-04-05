package edu.gemini.aspen.giapi.web.ui.vaadin.services

import java.net.URL
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.osgi.framework.Bundle
import org.osgi.framework.BundleContext
import edu.gemini.aspen.gds.api.Predef._
import scala.collection.JavaConversions._

/**
 * Servlet that can serve static resources needed by Vaadin
 */
class StaticResources(ctx: BundleContext) extends HttpServlet {
  val vaadinBundle: Option[Bundle] = findVaadinBundle(ctx)
  val widgetBundles: List[Option[Bundle]] = vaadinBundle :: findWidgetBundles(ctx)

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
    val path = req.getPathInfo
    val resourcePath = "VAADIN" + path

    // Find the bundle with the resource
    val sourceBundle = findBundleWithResource(resourcePath, widgetBundles)

    // Find the resource
    val u = sourceBundle flatMap {
      b => Option(b.getResource(resourcePath))
    }
    // Send the resource in the output stream
    u.map { ur: URL =>
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
    }.getOrElse {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND)
    }
  }

  /**
   * Finds what bundle contains the given resource
   */
  private def findBundleWithResource(resourcePath:String, bundles:List[Option[Bundle]]):Option[Bundle] = {
    bundles collect {
      // Discard non defined bundles
      case Some(b) => b
    } find {
      // Select the bundle that contains the resource
      b => Option(b.getResource(resourcePath)).isDefined
    }
  }

  /**
   * Finds the Vaadin Bundle
   */
  private def findVaadinBundle(ctx: BundleContext): Option[Bundle] =
    ctx.getBundles.toList.find {
      v: Bundle => "com.vaadin".equals(v.getSymbolicName())
    }

  /**
   * Finds any Bundle with a widgetset headers wrapped in an option
   */
  private def findWidgetBundles(ctx: BundleContext): List[Option[Bundle]] =
    ctx.getBundles().toList collect {
      case b: Bundle => (b, b.getHeaders)
    } filter {
      case (b, h) => h != null && h.toMap.containsKey("Vaadin-Widgetsets")
    } collect {
      case (b, _) => Option(b)
    }
}
