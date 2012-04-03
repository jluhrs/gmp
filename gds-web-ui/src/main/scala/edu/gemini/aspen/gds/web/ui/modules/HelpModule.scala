package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.terminal.ExternalResource
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Embedded

/**
 * Component containing an iframe with help as html
 */
class HelpModule extends GDSWebModule {
  val title = "GDS Help"
  val order = 4

  override def buildTabContent(app: Application) =
    new Embedded(caption = "GDS Help", source = new ExternalResource("docs/gds_user_manual.html"), objectType = com.vaadin.ui.Embedded.TYPE_BROWSER) {
      setSizeFull()
    }
}