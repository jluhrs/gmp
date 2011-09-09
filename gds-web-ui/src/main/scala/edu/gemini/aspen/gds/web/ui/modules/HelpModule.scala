package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.ui.{Embedded, VerticalLayout, Panel}
import com.vaadin.terminal.ExternalResource

/**
 * Component containing an iframe with help as html
 */
class HelpModule extends GDSWebModule {
  val title = "GDS Help"
  val order = 4

  override def buildTabContent(app: Application) = {
    val e = new Embedded("GDS Help", new ExternalResource("docs/gds_user_manual.html"));
    e.setType(Embedded.TYPE_BROWSER);
    e.setSizeFull()

    e
  }
}