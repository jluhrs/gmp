package edu.gemini.aspen.giapi.web.ui

import com.vaadin.event.ItemClickEvent
import com.vaadin.data.Property
import com.vaadin.ui.{LoginForm, Button}

/**
 * Utility methods to make vaadin easier to use with scala
 */
package object vaadin {
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
  implicit def intToUnitExtent(value: Int): UnitExtent = new UnitExtent(value)

  /**
   * Implicit conversions of Doubles to UnitExtent */
  implicit def doubleToUnitExtent(value: Double): UnitExtent = new UnitExtent(value)
}