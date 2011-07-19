package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.ui.{VerticalLayout, Panel}

/**
 * Component containing an iframe with help as html
 */
class HelpModule extends GDSWebModule {
    val title = "GDS Help"
    val order = 2

    override def buildTabContent(app: Application) = {
      val layout = new VerticalLayout
      layout.setSizeFull()
      layout
    }
}