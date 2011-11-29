package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import com.vaadin.data.util.ObjectProperty
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Label
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.HorizontalLayout
import com.vaadin.ui.Alignment

/**
 * Generic Status Panel Module that displays two labels next to each other
 */
abstract class AbstractStatusPanelModule extends StatusPanelModule {
  val label: String
  val item: String
  val property: ObjectProperty[_]
  lazy val itemLabel = new Label(label, style = "gds-status")
  lazy val itemValue = new Label(item, style = "gds-status", property = property)

  def buildModule = new HorizontalLayout(height = 100 percent, spacing = true) {
    add(itemLabel, alignment = Alignment.MIDDLE_LEFT)
    add(itemValue, alignment = Alignment.MIDDLE_RIGHT)
  }
}