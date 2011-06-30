package edu.gemini.aspen.gds.web.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.api.GDSWebModule
import com.vaadin.ui.{Label, Panel}

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModule]))
class AboutModule extends GDSWebModule {
    val title = "About"
    val order = 0

    override  def buildTabContent: com.vaadin.ui.Component = {
        val l1 = new Label("About GDS")
        val mainPanel = new Panel()
        mainPanel.setSizeFull
        mainPanel.addComponent(l1)
        mainPanel
    }
}