package edu.gemini.aspen.gmp.web.ui.status

import com.vaadin.terminal.gwt.server.AbstractApplicationServlet
import com.vaadin.Application
import javax.servlet.Servlet
import javax.servlet.http.{HttpSession, HttpServletRequest}
import org.apache.felix.ipojo.annotations.{Requires, Validate, Component => IPOJOComponent, Provides, Instantiate, ServiceProperty}
import java.util.Properties
import org.apache.felix.ipojo.{InstanceManager, ComponentInstance, Factory}
import java.util.logging.{Level, Logger}

@IPOJOComponent
@Provides(specifications = Array[Class[_]](classOf[Servlet]))
@Instantiate
class GMPStatusServlet(@Requires(from = "GMPStatusApp") val vaadinAppFactory: Factory) extends AbstractApplicationServlet {
  private val LOG = Logger.getLogger(this.getClass.getName)

  @ServiceProperty(name = "alias", value = "/gmp/status")
  val label: String = "/gmp/status"
  @ServiceProperty(name = "servlet-name", value = "GMPStatusServlet")
  val servletName: String = "GMPStatusServlet"
  var sessions = List[VaadinSession]()

  override def getNewApplication(request: HttpServletRequest) = {
    val app = try {
      val instance = vaadinAppFactory.createComponentInstance(new Properties())

      if (instance.getState() == ComponentInstance.VALID) {
        val application = instance.asInstanceOf[InstanceManager].getPojoObject.asInstanceOf[Application]
        sessions = VaadinSession(request.getSession(), application) :: sessions
        Option(application)
      } else {
        LOG.severe("Cannot get an implementation object from an invalid instance")
        None
      }
    } catch {
      case e: Exception => {
        LOG.log(Level.SEVERE, "Cannot get an implementation object from an invalid instance", e)
        None
      }
    }
    app.get
  }

  @Validate
  def validated() {
    LOG.info("Registered servlet under path " + label);
  }

  def getApplicationClass = classOf[Application]

  case class VaadinSession(session: HttpSession, application: Application) {
    require(session != null)
    require(application != null)

    def dispose() {
      application.close()
      session.invalidate()
    }
  }

}