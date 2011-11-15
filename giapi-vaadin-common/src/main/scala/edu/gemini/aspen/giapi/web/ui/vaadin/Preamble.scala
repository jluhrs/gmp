package edu.gemini.aspen.giapi.web.ui.vaadin

import com.vaadin.event.ItemClickEvent
import com.vaadin.data.Property
import com.vaadin.ui.{LoginForm, Button}

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

  implicit def login(func: (LoginForm#LoginEvent) => Unit) = new LoginForm.LoginListener {
      def onLogin(event: LoginForm#LoginEvent) {
        func(event)
      }
  }
}