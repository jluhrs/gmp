package edu.gemini.aspen.gpi.web.ui.aoplot

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.annotations._
import java.util.Properties
import java.util.logging.{Level, Logger}
import org.ops4j.pax.web.service.WebContainer
import com.vaadin.ui.Window
import javax.servlet.{ServletConfig, Servlet}
import org.icepush.servlet.MainServlet
import org.vaadin.artur.icepush.{ICEPush, ICEPushServlet}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpSession}
import org.icepush.CodeServer
import java.io.{InputStreamReader, InputStream, BufferedWriter}

import org.icepush.CodeServer
import org.vaadin.artur.icepush.JavascriptLocations
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import edu.gemini.aspen.gmp.epics.{EpicsUpdate, EpicsUpdateListener, EpicsRegistrar}

object OSGiJavascriptProvider {
  private def quote(string: String): String = {
    return "'" + string + "'"
  }

  private def read(input: InputStream): StringBuilder = {
    var sb: StringBuilder = new StringBuilder(input.available)
    var isr: InputStreamReader = new InputStreamReader(input)
    var buf: Array[Char] = new Array[Char](4096)
    var len: Int = 0
    while ((({
      len = isr.read(buf); len
    })) > -1) {
      sb.append(buf, 0, len)
    }
    return sb
  }
}

class OSGiJavascriptProvider {
  /**
   * Constructor for servlet based push where a base url can be used.
   *
   * @param baseUrl
   * @throws IOException
   */
  def this(baseUrl: String) {
    this ()
    jsLocations = new JavascriptLocations(baseUrl)
    init
  }

  def this(codeURL: String, createPushIdURL: String, addGroupMemberURL: String, removeGroupMemberURL: String, listenURL: String, notifyURL: String) {
    this ()
    jsLocations = new JavascriptLocations(codeURL, createPushIdURL, addGroupMemberURL, removeGroupMemberURL, listenURL, notifyURL)
    init
  }

  def getJavaScript: String = {
    return javaScript
  }

  private def init: Unit = {
    var code: InputStream = classOf[OSGiJavascriptProvider].getResourceAsStream("/icepush-modified.js")
    javaScript = OSGiJavascriptProvider.read(code).toString
    javaScript = javaScript.replace("calculateURI('create-push-id.icepush')", OSGiJavascriptProvider.quote(jsLocations.getCreatePushId))
    javaScript = javaScript.replace("calculateURI('add-group-member.icepush')", OSGiJavascriptProvider.quote(jsLocations.getAddGroupMember))
    javaScript = javaScript.replace("calculateURI('listen.icepush')", OSGiJavascriptProvider.quote(jsLocations.getListen))
    javaScript = javaScript.replace("calculateURI('notify.icepush')", OSGiJavascriptProvider.quote(jsLocations.getNotify))
    javaScript = javaScript.replace("calculateURI('remove-group-member.icepush')", OSGiJavascriptProvider.quote(jsLocations.getRemoveGroupMember))
  }

  def getCodeLocation: String = {
    return jsLocations.getCode.toString
  }

  def getCodeName: String = {
    return "code.icepush"
  }

  private var javaScript: String = null
  private var jsLocations: JavascriptLocations = null
}

/**
 * A Generic Vaadin servlet that can delegate to an iPojo factory the creation of a Vaadin applicat
 *
 * TODO Abstract away the
 */
@Component
@Provides(specifications = Array(classOf[Servlet]))
@Instantiate
class VaadinServlet(@Requires(from = "GPIAOFactory") val vaadinAppFactory: Factory, @Requires val webContainer: WebContainer, @Requires epicsRegistrar:EpicsRegistrar) extends AbstractApplicationServlet {
  private val LOG = Logger.getLogger(this.getClass.getName)
  @ServiceProperty(name = "alias", value = "/gpi/ao")
  val label: String = "/gpi/ao"
  @ServiceProperty(name = "servlet-name", value = "GDSVaadinServlet")
  val servletName: String = "GPIAOVaadinServlet"
  @ServiceProperty(name = "widgetset", value = "edu.gemini.aspen.gpi.web.ui.aoplot.GPIAOPlot")
  val widgetset: String = "edu.gemini.aspen.gpi.web.ui.aoplot.GPIAOPlot"
  var sessions = List[VaadinSession]()
  var icePushServlet: MainServlet = null
  var javascriptProvider: OSGiJavascriptProvider = null


  override def init(servletConfig: ServletConfig) {
    super.init(servletConfig);
    icePushServlet = new MainServlet(servletConfig.getServletContext())
  epicsRegistrar.registerInterest("ws:cpWf", new EpicsUpdateListener(){
    def onEpicsUpdate(update: EpicsUpdate) {
      println("Update " + update)
    }
  })
    /*String codeBasePath = servletConfig.getServletContext().getContextPath();
String codeLocation = servletConfig.getInitParameter("codeBasePath");
if (codeLocation != null)
codeBasePath = codeLocation;
if (codeBasePath.endsWith("/"))
codeBasePath = codeBasePath.substring(0, codeBasePath.length() - 2);*/
    javascriptProvider = new OSGiJavascriptProvider(label);
    ICEPush.setCodeJavascriptLocation(this.javascriptProvider.getCodeLocation);
  }

  override def service(request:HttpServletRequest , response:HttpServletResponse ) {
    val pathInfo = request.getPathInfo()
    if (pathInfo != null && pathInfo.equals("/" + javascriptProvider.getCodeName)) {
      // Serve icepush.js
      serveIcePushCode(response);
      return;
    }

    if (request.getRequestURI().endsWith(".icepush")) {
      // Push request
        this.icePushServlet.service(request, response);
    } else {
      // Vaadin request
      super.service(request, response);
    }
  }

   private def serveIcePushCode(response:HttpServletResponse ) {
        val icepushJavscript = this.javascriptProvider.getJavaScript;
        response.setHeader("Content-Type", "text/javascript");
        response.getOutputStream().write(icepushJavscript.getBytes());
    }

    override def destroy() {
        super.destroy()
        icePushServlet.shutdown()
    }

    override def getApplicationClass() =
    {
      classOf[Application]
    }

    override def writeAjaxPageHtmlVaadinScripts(window: Window,
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
    }

    override protected def getNewApplication(request: HttpServletRequest): Application =
    {
      val app = try {
        val instance = vaadinAppFactory.createComponentInstance(new Properties())

        if (instance.getState() == ComponentInstance.VALID) {
          val application = instance.asInstanceOf[InstanceManager].getPojoObject.asInstanceOf[GPIAOVaadinApp]
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
