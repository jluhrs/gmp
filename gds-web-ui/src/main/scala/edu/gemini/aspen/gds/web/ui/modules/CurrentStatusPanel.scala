package edu.gemini.aspen.gds.web.ui.modules

import com.vaadin.ui._
import org.apache.felix.ipojo.annotations.Bind._
import org.apache.felix.ipojo.annotations.{Unbind, Bind, Provides, Instantiate}
import org.apache.felix.ipojo.annotations.Unbind._
import java.util.logging.Logger
import edu.gemini.aspen.gds.web.ui.api.{StatusPanelModule, GDSWebModule, StatusPanel}



@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class CurrentStatusPanel extends AbstractStatusPanelModule {
    val label:String = "Status"
    val item:String = "Running"

}



