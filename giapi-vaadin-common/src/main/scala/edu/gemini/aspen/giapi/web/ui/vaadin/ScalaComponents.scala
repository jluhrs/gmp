package edu.gemini.aspen.giapi.web.ui.vaadin

import com.vaadin.data.Property

class Label(caption:String = null, contentMode:Int = com.vaadin.ui.Label.CONTENT_DEFAULT, style:String=null, property:Property = null) extends com.vaadin.ui.Label(caption, contentMode) {
  setStyleName(style)
  setPropertyDataSource(property)
}