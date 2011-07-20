package edu.gemini.aspen.gds.web.ui.api

import com.vaadin.ui._

/**
 * Utility methods to make vaadin easier to use with scala
 */
object Preamble {

  implicit def buttonClickWrapper(func: (Button#ClickEvent) => Any) =
    new Button.ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        func(e)
      }
    }

  implicit def buttonClickWrapperToUnit(func: (Button#ClickEvent) => Unit) =
    new Button.ClickListener {
      def buttonClick(e: Button#ClickEvent) {
        func(e)
      }
    }
}