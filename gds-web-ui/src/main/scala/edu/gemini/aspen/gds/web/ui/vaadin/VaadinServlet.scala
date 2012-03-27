package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.annotations._
import java.util.Properties
import java.util.logging.{Level, Logger}
import org.ops4j.pax.web.service.WebContainer
import com.vaadin.ui.Window
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.VerticalLayout
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Label
import com.vaadin.ui.Window.Notification

/**
 * A Generic Vaadin servlet that can delegate to an iPojo factory the creation of a Vaadin applicat
 *
 * TODO Abstract away the
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[Servlet]))
class VaadinServlet(@Requires(from = "VaadinAppFactory") val vaadinAppFactory: Factory, @Requires val webContainer: WebContainer) extends AbstractApplicationServlet {
  private val LOG = Logger.getLogger(this.getClass.getName)
  @ServiceProperty(name = "alias", value = "/gds")
  val label: String = "/gds"
  @ServiceProperty(name = "servlet-key", value = "GDSVaadinServlet")
  val servletName: String = "GDSVaadinServlet"
  @ServiceProperty(name = "widgetset", value = "GDSCoreVaadinApp")
  val widgetset: String = "GDSCoreVaadinApp"
  var sessions = List[VaadinSession]()

  override def getApplicationClass() = {
    classOf[Application]
  }

  override protected def getNewApplication(request: HttpServletRequest): Application = {
    val app: Either[String, Application] = try {
      val instance = vaadinAppFactory.createComponentInstance(new Properties())

      if (instance.getState() == ComponentInstance.VALID) {
        val application = instance.asInstanceOf[InstanceManager].getPojoObject.asInstanceOf[GDSCoreVaadinApp]
        sessions = VaadinSession(request.getSession(), application) :: sessions
        Right(application)
      } else {
        LOG.severe("Cannot get an implementation object from an invalid instance")
        Left("The requested iPojo instance GDSCoreVaadinApp is invalid, contact the administrator")
      }
    } catch {
      case e: Exception => {
        LOG.log(Level.SEVERE, "Cannot get an implementation object from an invalid instance", e)
        Left("There is an error when burding the GDS Web UT, contact the administrator\nerror: " + e.getMessage + "\n" + e.getStackTraceString)
      }
    }
    app match {
      case Right(app) => app
      case Left(msg) => new Application {
        def init() {
          setMainWindow(new LoadingErrorWindow(msg))
          getMainWindow.showNotification("Error while starting the app", Notification.TYPE_ERROR_MESSAGE)
        }
      }
    }
  }

  @Validate
  def validated() {
    LOG.info("Registered servlet under path " + label)
  }

  case class VaadinSession(session: HttpSession, application: Application) {
    require(session != null)
    require(application != null)

    def dispose() {
      application.close()
      session.invalidate()
    }
  }

}

class LoadingErrorWindow(msg:String) extends Window {
  setContent(new VerticalLayout(sizeFull = true) {
    add(new Label(msg, contentMode = com.vaadin.ui.Label.CONTENT_PREFORMATTED), ratio = 1f)
  })
}