package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate}
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import com.vaadin.data.util.ObjectProperty
import java.net.InetAddress

@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class HostnameStatusPanel extends AbstractStatusPanelModule {
  val order = 2
  val label = "Hostname:"
  val item = InetAddress.getLocalHost.getHostName
  val property = new ObjectProperty[String](item)
}