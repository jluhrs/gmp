package edu.gemini.aspen.gpi.web.ui.aoplot


import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.api.Predef._
import org.atmosphere.cpr.AtmosphereServlet
import javax.servlet.{ServletConfig, Servlet}
import java.net.{URLClassLoader, URL}
import org.atmosphere.util.AtmosphereConfigReader
import org.vaadin.dontpush.server.AtmosphereDontPushHandler

/**
 * Servlet that can serve static resources needed by Vaadin
 *
 * TODO Replace with a pax-extender resource bundle
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[Servlet]))
class AOAtmosphereServlet(ctx: BundleContext) extends AtmosphereServlet {
  @ServiceProperty(name = "alias", value = "/UIDL")
  val label: String = "/UIDL"
  @ServiceProperty(name = "servlet-name", value = "AOAtmosphereServlet")
  val servletName: String = "AOAtmosphereServlet"
  @ServiceProperty(name = "org.atmosphere.disableOnStateEvent", value = "true")
  val disableOnStateEvent: String = "true"

  override def loadConfiguration(sc:ServletConfig) {
    val url = ctx.getBundles().toList filter {
      v: Bundle => "edu.gemini.external.osgi.org.vaadin.dontpush-addon-ozonelayer".equals(v.getSymbolicName())
  } head
    

    val urlC = new URLClassLoader(Array[URL](url.getResource("/")),
                classOf[AtmosphereDontPushHandler].getClassLoader)
    //var urlC = classOf[AtmosphereDontPushHandler].getClassLoader
    println(url.getResource("/"))
    println(urlC)
    println(urlC.loadClass("org.vaadin.dontpush.server.AtmosphereDontPushHandler"))
    loadAtmosphereDotXml(sc.getServletContext().
              getResourceAsStream("/META-INF/atmosphere.xml"), urlC);

            /*if (atmosphereHandlers.size() == 0) {
                autoDetectAtmosphereHandlers(sc.getServletContext(), urlC);

                if (atmosphereHandlers.size() == 0) {
                    detectSupportedFramework(sc);
                }
            }*/
  }
}
