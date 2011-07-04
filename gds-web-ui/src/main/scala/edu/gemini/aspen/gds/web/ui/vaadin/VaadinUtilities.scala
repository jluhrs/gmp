package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.ui._

/**
 * Utility methods to make vaadin easier to use with scala
 */
object VaadinUtilities {

  implicit def buttonClickWrapper(func: (Button#ClickEvent) => Unit) =
    new Button.ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        func(e)
      }
    }
}