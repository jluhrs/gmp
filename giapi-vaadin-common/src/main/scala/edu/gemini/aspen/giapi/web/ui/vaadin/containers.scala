package edu.gemini.aspen.giapi.web.ui.vaadin

import scala.collection.JavaConverters._
import com.vaadin.ui.Component

package containers {
class SelectedTabChangeListener(action: com.vaadin.ui.TabSheet#SelectedTabChangeEvent => Unit) extends com.vaadin.ui.TabSheet.SelectedTabChangeListener {
  def selectedTabChange(event: com.vaadin.ui.TabSheet#SelectedTabChangeEvent) = action(event)
}

class TabSheet(width: String = 100 percent, height: String = null, caption: String = null, style: String = null, size: Tuple2[String, String] = null)
  extends com.vaadin.ui.TabSheet() {
  setCaption(caption)
  if (size != null) {
    setWidth(size._1)
    setHeight(size._2)
  } else {
    setWidth(width)
    setHeight(height)
  }
  setStyleName(style)

  def addListener(action: com.vaadin.ui.TabSheet#SelectedTabChangeEvent => Unit): Unit = addListener(new SelectedTabChangeListener(action))

  def getComponents(): TraversableOnce[Component] = getComponentIterator.asScala.toSeq
}
}