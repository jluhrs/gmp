package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.ui.{Window, Label, Panel}

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModule]))
class AboutModule extends GDSWebModule {
    val title = "About"
    val order = 2

    override  def buildTabContent(mainWindow:Window): com.vaadin.ui.Component = {
        val l1 = new Label("About GDS")
        val mainPanel = new Panel()
        mainPanel.setSizeFull
        mainPanel.addComponent(l1)
        mainPanel
    }
}