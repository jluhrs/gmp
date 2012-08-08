package edu.gemini.aspen.giapi.web.ui.vaadin

import scala.collection.JavaConverters._
import com.vaadin.ui.Component

package containers {

import com.vaadin.ui.ComponentContainer
import com.vaadin.terminal.Resource

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

class Panel(caption: String = null, width: String = 100 percent, height: String = null, style: String = null, sizeFull: Boolean = false)
  extends com.vaadin.ui.Panel() {
  setCaption(caption)
  setWidth(width)
  setHeight(height)
  setStyleName(style)
  if (sizeFull) setSizeFull()

  def getComponents(): TraversableOnce[Component] = getComponentIterator.asScala.toSeq

  def add[C <: com.vaadin.ui.Component](component: C = null): C = {
    addComponent(component)
    component
  }
}

class Window(caption: String = null, width: String = null, height: String = null, content: ComponentContainer = null, modal: Boolean = false, icon: Resource = null, style: String = null, resizable: Boolean = true, draggable: Boolean = true, closable: Boolean = true)
  extends com.vaadin.ui.Window(caption, content) {
  setWidth(width)
  setHeight(height)
  setModal(modal)
  setIcon(icon)
  setStyleName(style)
  setResizable(resizable)
  setClosable(closable)
  setDraggable(draggable)

  def add[C <: com.vaadin.ui.Component](component: C = null): C = {
    addComponent(component)
    component
  }

  def getComponents(): TraversableOnce[Component] = getComponentIterator.asScala.toSeq
}
class HorizontalSplitPanel(width: String = 100 percent, height: String = 100 percent, caption: String = null, style: String = null)
  extends com.vaadin.ui.HorizontalSplitPanel() {
  setWidth(width)
  setHeight(height)
  setCaption(caption)
  setStyleName(style)

  def add[C <: com.vaadin.ui.Component](component: C = null): C = {
    addComponent(component)
    component
  }

  def getComponents(): TraversableOnce[Component] = getComponentIterator.asScala.toSeq
}

class VerticalSplitPanel(width: String = 100 percent, height: String = 100 percent, caption: String = null, style: String = null)
  extends com.vaadin.ui.VerticalSplitPanel() {
  setWidth(width)
  setHeight(height)
  setCaption(caption)
  setStyleName(style)

  def add[C <: com.vaadin.ui.Component](component: C = null): C = {
    addComponent(component)
    component
  }

  def getComponents(): TraversableOnce[Component] = getComponentIterator.asScala.toSeq
}

}