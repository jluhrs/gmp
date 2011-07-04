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

/**
 * A Generic Vaadin servlet that can delegate to an iPojo factory the creation of a Vaadin applicat
 *
 * TODO Abstract away the
 */
@Component
@Provides(specifications = Array(classOf[Servlet]))
@Instantiate
class VaadinServlet(@Requires(from = "VaadinAppFactory") val vaadinAppFactory: Factory) extends AbstractApplicationServlet {
    private val LOG = Logger.getLogger(this.getClass.getName)
    @ServiceProperty(name = "alias", value = "/gds")
    val label: String = "/gds"
    @ServiceProperty(name = "servlet-name", value = "GDSVaadinServlet")
    val servletName: String = "GDSVaadinServlet"
    var sessions = List[VaadinSession]()

    override def getApplicationClass() = {
        classOf[Application]
    }

    override protected def getNewApplication(request: HttpServletRequest): Application = {
        val app = try {
            val instance = vaadinAppFactory.createComponentInstance(new Properties())

            if (instance.getState() == ComponentInstance.VALID) {
                val application = instance.asInstanceOf[InstanceManager].getPojoObject.asInstanceOf[GDSCoreVaadinApp]
                sessions = VaadinSession(request.getSession(), application) :: sessions
                Option(application)
            } else {
                LOG.severe("Cannot get an implementation object from an invalid instance");
                None
            }
        } catch {
            case e:Exception => {
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
