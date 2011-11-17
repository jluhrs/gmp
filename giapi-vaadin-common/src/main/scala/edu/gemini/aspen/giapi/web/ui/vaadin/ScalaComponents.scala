package edu.gemini.aspen.giapi.web.ui.vaadin

class Label(caption:String = null, contentMode:Int = com.vaadin.ui.Label.CONTENT_DEFAULT, style:String=null) extends com.vaadin.ui.Label(caption, contentMode) {
  setStyleName(style)
}