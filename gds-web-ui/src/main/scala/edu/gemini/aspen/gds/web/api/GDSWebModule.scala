package edu.gemini.aspen.gds.web.api

import com.vaadin.ui.{Panel, Component}

/**
 * A service that implements a GDSWebModule will be added to the list of tabs and the
 * main area of the GDS Web interface
 */
trait GDSWebModule {
    /**
     * Title of the tab
     */
    val title:String

    /**
     * Order of the tab
     */
    val order:Int

    /**
     * Builds a component to be added to the tab
     *
     * Implementations should override this method to produce the actual content
     */
    def buildTabContent:Component = {new Panel()}
}