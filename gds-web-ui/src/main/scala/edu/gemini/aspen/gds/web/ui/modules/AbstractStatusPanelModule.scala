package edu.gemini.aspen.gds.web.ui.modules

import com.vaadin.ui._
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import com.vaadin.data.util.ObjectProperty

/**
 * Generic Status Panel Module that displays two labels next to each other
 */
abstract class AbstractStatusPanelModule extends StatusPanelModule {
  val label: String
  val item: String
  val property: ObjectProperty[_]
  lazy val itemLabel = new Label(label)
  lazy val itemValue = new Label(item)

  def buildModule = {
    val layout = new HorizontalLayout
    layout.setHeight("100%")
    layout.setWidth("200")

    itemLabel.setStyleName("gds-status")
    itemValue.setStyleName("gds-status")
    itemValue.setPropertyDataSource(property)

    layout.addComponent(itemLabel)
    layout.setComponentAlignment(itemLabel, Alignment.MIDDLE_LEFT)

    layout.addComponent(itemValue)
    layout.setComponentAlignment(itemValue, Alignment.MIDDLE_RIGHT)

    layout
  }
}





