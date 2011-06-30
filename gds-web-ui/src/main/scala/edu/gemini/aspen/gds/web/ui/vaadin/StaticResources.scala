package edu.gemini.aspen.gds.web.ui.vaadin

import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext
import org.apache.felix.ipojo.annotations._
import javax.servlet.Servlet
import edu.gemini.aspen.gds.api.Predef._
import java.io.{FileOutputStream, FileInputStream}

/**
 * Servlet that can serve static resources needed by Vaadin
 *
 * TODO Replace with a pax-extender resource bundle
 */
@Component
@Provides(specifications = Array(classOf[Servlet]))
@Instantiate
class StaticResources(ctx: BundleContext) extends HttpServlet {
    @ServiceProperty(name = "alias", value = "/VAADIN")
    val label: String = "/VAADIN"
    val vaadinBundle = findVaadinBundle(ctx)

    override def doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val path = req.getPathInfo
        val resourcePath = "VAADIN" + path

        val u = vaadinBundle map {_.getResource(resourcePath)}
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

    def findVaadinBundle(ctx: BundleContext) =
        ctx.getBundles().toList filter {
            v: Bundle => "com.vaadin".equals(v.getSymbolicName())
        } headOption
}
