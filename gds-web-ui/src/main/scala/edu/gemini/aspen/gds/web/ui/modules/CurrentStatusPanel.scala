package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate}
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import com.vaadin.data.util.ObjectProperty

@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class CurrentStatusPanel extends AbstractStatusPanelModule {
    val label:String = "Status"
    val item:String = "Running"
    val property = new ObjectProperty[String]("Running")
}



