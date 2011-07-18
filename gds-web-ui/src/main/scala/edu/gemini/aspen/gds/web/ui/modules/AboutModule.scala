package edu.gemini.aspen.gds.web.ui.modules

import com.vaadin.Application
import com.vaadin.ui.{Label, Panel}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule

/**
 * Tab containing an About message
 */
class AboutModule extends GDSWebModule {
  val title = "About"
  val order = 2
  
  override def buildTabContent(app: Application): com.vaadin.ui.Component = {
    val aboutLabel = new Label("About GDS")
    val mainPanel = new Panel()
    mainPanel.setSizeFull
    mainPanel.addComponent(aboutLabel)
    mainPanel
  }
}