package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.ui.{VerticalLayout, Label, Panel}
;

/**
 * Tab containing an About message
 */
class AboutModule extends GDSWebModule {
  val title = "About"
  val order = 3

  override def buildTabContent(app: Application): com.vaadin.ui.Component = {
    val aboutLabel = new Label("About GDS")
    val layout = new VerticalLayout
    layout.setSizeFull
    layout.addComponent(aboutLabel)
    layout
  }
}