package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.ui.{Panel, Window}

/**
 * Component containing an iframe with help as html
 */
class HelpModule extends GDSWebModule {
    val title = "GDS Help"
    val order = 1

    override def buildTabContent(mainWindow:Window) = new Panel()
}