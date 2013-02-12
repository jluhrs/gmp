package edu.gemini.aspen.giapi.web.ui

import com.vaadin.event.ItemClickEvent
import com.vaadin.data.Property
import com.vaadin.ui.{LoginForm, Button}

/**
 * Utility methods to make vaadin easier to use with scala
 */
package object vaadin {
  /*implicit def buttonClickWrapper(action: (Button#ClickEvent) => Any) =
    new Button.ClickListener {
      override def buttonClick(e: Button#ClickEvent) {
        action(e)
      }
    }

  implicit def buttonClickWrapperToUnit(action: (Button#ClickEvent) => Unit) =
    new Button.ClickListener {
      override def buttonClick(e: Button#ClickEvent) {
        action(e)
      }
    }*/

  implicit val itemClickWrapper = (action: (ItemClickEvent) => Unit) =>
    new ItemClickEvent.ItemClickListener {
      override def itemClick(event: ItemClickEvent) {
        action(event)
      }
    }

  /*implicit def propertyValueChangeWrapper(action: (Property.ValueChangeEvent) => Unit) =
    new Property.ValueChangeListener {
      override def valueChange(event: Property.ValueChangeEvent) {
        action(event)
      }
    }*/

  implicit val login = (action: (LoginForm#LoginEvent) => Unit) => new LoginForm.LoginListener {
    def onLogin(event: LoginForm#LoginEvent) {
      action(event)
    }
  }

  /**
   * Classes used to wrap measurement units in Vaadin */
  class UnitExtent(value: Number) {
    def px: String = value + "px"

    def percent: String = value + "%"

    def pct: String = value + "%"

    def em: String = value + "em"

    def ex: String = value + "ex"

    def in: String = value + "in"

    def cm: String = value + "cm"

    def mm: String = value + "mm"

    def pt: String = value + "pt"

    def pc: String = value + "pc"
  }

  /**
   * Implicit conversions of Integer to UnitExtent */
  implicit class intToUnitExtent(value: Int) extends UnitExtent(value)

  /**
   * Implicit conversions of Doubles to UnitExtent */
  implicit class doubleToUnitExtent(value: Double) extends UnitExtent(value)
}