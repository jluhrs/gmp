package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import com.vaadin.ui.{Label, Panel}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule

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