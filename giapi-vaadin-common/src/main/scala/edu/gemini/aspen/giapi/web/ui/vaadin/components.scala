package edu.gemini.aspen.giapi.web.ui.vaadin

import com.vaadin.data.Property
import com.vaadin.terminal.Resource

package components {

import com.vaadin.ui.themes.BaseTheme

/**
 * Scala wrapper for com.vaadin.ui.Label */
class Label(content: String = null,
            caption: String = null,
            contentMode: Int = com.vaadin.ui.Label.CONTENT_DEFAULT,
            style: String = null,
            property: Property = null)
  extends com.vaadin.ui.Label(content, contentMode) {
  setStyleName(style)
  setCaption(caption)

  Option(property) foreach setPropertyDataSource
}

/**
 * Scala wrapper for com.vaadin.ui.Button */
class Button(caption: String = null,
             action: com.vaadin.ui.Button#ClickEvent => Unit = null,
             icon: Resource = null,
             style: String = null,
             enabled: Boolean = true,
             description: String = null)
  extends com.vaadin.ui.Button(caption) {
  setIcon(icon)
  setStyleName(style)
  setEnabled(enabled)

  Option(action) foreach addListener

  protected[vaadin] class ButtonClickListener(action: com.vaadin.ui.Button#ClickEvent => Unit) extends com.vaadin.ui.Button.ClickListener {
    override def buttonClick(event: com.vaadin.ui.Button#ClickEvent) = action(event)
  }

  def addListener(action: com.vaadin.ui.Button#ClickEvent => Unit): Unit = addListener(new ButtonClickListener(action))
}

/**
 * Scala wrapper for com.vaadin.ui.Button with style Link */
class LinkButton(caption: String = null,
                 action: com.vaadin.ui.Button#ClickEvent => Unit = null,
                 icon: Resource = null,
                 style: String = null,
                 enabled: Boolean = true,
                 description: String = null)
  extends Button(caption, action, icon, style, enabled, description) {
  setStyleName(BaseTheme.BUTTON_LINK)
}

/**
 * Scala wrapper for com.vaadin.ui.Embedded */
class Embedded(caption: String = null,
               width: String = null,
               height: String = null,
               source: Resource = null,
               objectType: Int = com.vaadin.ui.Embedded.TYPE_OBJECT,
               style: String = null)
  extends com.vaadin.ui.Embedded(caption) {
  setWidth(width)
  setHeight(height)
  setType(objectType)
  setStyleName(style)

  Option(source) foreach setSource
}

/**
 * Scala wrapper for com.vaadin.ui.TextField */
class TextField(caption: String = null,
                width: String = null,
                height: String = null,
                property: com.vaadin.data.Property = null,
                value: Any = null,
                style: String = null,
                prompt: String = null,
                maxLength: Int = -1,
                invalidAllowed:Boolean = true,
                required: Boolean = true,
                immediate: Boolean = false)
  extends com.vaadin.ui.TextField(caption) {
  setWidth(width)
  setHeight(height)
  setStyleName(style)
  setInputPrompt(prompt)
  setRequired(required)
  setImmediate(immediate)
  setInvalidAllowed(invalidAllowed)

  Option(property) foreach setPropertyDataSource
  Option(value) foreach setValue
  Option(maxLength) foreach setMaxLength
}

}