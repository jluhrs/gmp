package edu.gemini.aspen.gds.web.ui.api

import com.vaadin.ui._
import com.vaadin.event.ItemClickEvent
import com.vaadin.data.Item.PropertySetChangeEvent
import com.vaadin.data.{Property, Container}
import com.vaadin.ui.Window._
import com.vaadin.ui.LoginForm.LoginListener

/**
 * Utility methods to make vaadin easier to use with scala
 */
object Preamble {

  implicit def buttonClickWrapper(func: (Button#ClickEvent) => Any) =
    new Button.ClickListener {
      override def buttonClick(e: Button#ClickEvent) {
        func(e)
      }
    }

  implicit def buttonClickWrapperToUnit(func: (Button#ClickEvent) => Unit) =
    new Button.ClickListener {
      override def buttonClick(e: Button#ClickEvent) {
        func(e)
      }
    }

  implicit def itemClickWrapper(func: (ItemClickEvent) => Unit) =
    new ItemClickEvent.ItemClickListener {
      override def itemClick(event: ItemClickEvent) {
        func(event)
      }
    }

  implicit def propertyValueChangeWrapper(func: (Property.ValueChangeEvent) => Unit) =
    new Property.ValueChangeListener {
      override def valueChange(event: Property.ValueChangeEvent) {
        func(event)
      }
    }

  implicit def login(func: (LoginForm#LoginEvent) => Unit) = new LoginListener {
      def onLogin(event: LoginForm#LoginEvent) {
        func(event)
      }
  }
}