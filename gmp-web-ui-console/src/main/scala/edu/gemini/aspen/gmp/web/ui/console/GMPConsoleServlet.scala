package edu.gemini.aspen.gmp.web.ui.console

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.annotations._
import java.util.Properties
import java.util.logging.{Level, Logger}
import com.vaadin.ui.Window
import javax.servlet.{ServletConfig, Servlet}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpSession}
import java.io.{InputStreamReader, InputStream, BufferedWriter}
import java.net.{URLClassLoader, URL}
import org.osgi.framework.BundleContext

/**
 * A Generic Vaadin servlet that can delegate to an iPojo factory the creation of a Vaadin applicat
 *
 * TODO Abstract away the
 */
@Component
@Provides(specifications = Array(classOf[Servlet]))
@Instantiate
class GMPConsoleServlet(@Requires(from = "GMPConsoleApp") val vaadinAppFactory: Factory) extends AbstractApplicationServlet {
  private val LOG = Logger.getLogger(this.getClass.getName)
  @ServiceProperty(name = "alias", value = "/gmp/console")
  val label: String = "/gmp/console"
  @ServiceProperty(name = "servlet-name", value = "GMPConsoleServlet")
  val servletName: String = "GMPConsoleServlet"
  @ServiceProperty(name = "widgetset", value = "edu.gemini.aspen.gmp.web.ui.console.GMPConsole")
  val widgetset: String = "edu.gemini.aspen.gmp.web.ui.console.GMPConsole"
  var sessions = List[VaadinSession]()


    override def getApplicationClass() =
    {
      classOf[Application]
    }

    /*override def writeAjaxPageHtmlVaadinScripts(window: Window,
      themeName: String, application: Application, page: BufferedWriter,
      appUrl: String, themeUri: String, appId: String,
      request: HttpServletRequest)
    {
      page.write("<script type=\"text/javascript\">\n")
      page.write("//<![CDATA[\n")
      page.write("document.write(\"<script language='javascript' src='./js/jquery-1.4.4.min.js'><\\/script>\");\n")
      page.write("document.write(\"<script language='javascript' src='./js/highcharts.js'><\\/script>\");\n")
      page.write("document.write(\"<script language='javascript' src='./js/modules/exporting.js'><\\/script>\");\n")
      page.write("//]]>\n</script>\n")
      super.writeAjaxPageHtmlVaadinScripts(window, themeName, application,
        page, appUrl, themeUri, appId, request)
    }*/

    override protected def getNewApplication(request: HttpServletRequest): Application =
    {
      val app = try {
        val instance = vaadinAppFactory.createComponentInstance(new Properties())

        if (instance.getState() == ComponentInstance.VALID) {
          val application = instance.asInstanceOf[InstanceManager].getPojoObject.asInstanceOf[GMPConsoleApp]
          sessions = VaadinSession(request.getSession(), application) :: sessions
          Option(application)
        } else {
          LOG.severe("Cannot get an implementation object from an invalid instance");
          None
        }
      } catch {
        case e: Exception => {
          LOG.log(Level.SEVERE, "Cannot get an implementation object from an invalid instance", e);
          None
        }
      }
      app.get
    }

    @Validate
    def validated() {
      LOG.info("Registered servlet under path " + label);
    }

    case class VaadinSession(session: HttpSession, application: Application) {
      require(session != null)
      require(application != null)

      def dispose() {
        application.close();
        session.invalidate();
      }
    }

}
