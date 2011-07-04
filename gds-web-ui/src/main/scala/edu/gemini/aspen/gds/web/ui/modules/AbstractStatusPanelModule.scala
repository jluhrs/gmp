package edu.gemini.aspen.gds.web.ui.modules

import com.vaadin.ui._
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule

/**
 * Generic Status Panel Module that displays two labels next to each other
 */
abstract class AbstractStatusPanelModule extends StatusPanelModule {
    val label:String
    val item:String

    def buildModule = {
        val layout = new HorizontalLayout
        layout.setHeight("100%")
        layout.setWidth("200")

        val statusLabel = new Label(label)
        val statusState = new Label(item)

        layout.addComponent(statusLabel)
        layout.setComponentAlignment(statusLabel, Alignment.MIDDLE_LEFT)

        layout.addComponent(statusState)
        layout.setComponentAlignment(statusState, Alignment.MIDDLE_RIGHT)

        layout
    }
}





