package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.giapi.web.ui.vaadin.components._
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.VerticalLayout
import com.vaadin.Application
import com.vaadin.ui.Alignment

/**
 * Tab containing an About message
 */
class AboutModule extends GDSWebModule {
  val title = "About"
  val order = 5

  override def buildTabContent(app: Application): com.vaadin.ui.Component =
    new VerticalLayout(sizeFull = true) {
      val aboutLabel = new Label("About GDS", style = "about")
      add(aboutLabel, alignment = Alignment.TOP_CENTER)
    }
}