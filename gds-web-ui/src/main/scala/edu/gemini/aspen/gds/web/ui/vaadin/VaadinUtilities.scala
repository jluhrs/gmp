package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.Application;
import org.apache.felix.ipojo.annotations.{Component, Bind, Unbind}
import java.util.logging.Logger
import com.vaadin.terminal.{ClassResource, StreamResource}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.ui.Component._
import com.vaadin.event.ItemClickEvent
import com.vaadin.event.ItemClickEvent.ItemClickListener
import javax.mail.FetchProfile.Item
import com.vaadin.event.MouseEvents.{ClickEvent, ClickListener}
import com.vaadin.ui._
import themes.BaseTheme

object VaadinUtilities {
    implicit def actionPerformedWrapper(func: (Button#ClickEvent) => Unit) =
        new Button.ClickListener {
            def buttonClick(e: Button#ClickEvent) {
                func(e)
            }
        }
}

/**
 * Main page of the GDS web UI
 */

