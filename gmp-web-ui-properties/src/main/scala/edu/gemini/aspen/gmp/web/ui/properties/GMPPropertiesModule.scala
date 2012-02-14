package edu.gemini.aspen.gmp.web.ui.properties

import _root_.edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import edu.gemini.aspen.gmp.services.PropertyHolder
import com.vaadin.ui._
import edu.gemini.aspen.gmp.services.properties.GmpProperties

/**
 * Module for the table to edit the keywords
 */
class GMPPropertiesModule(propertyHolder: PropertyHolder) extends GDSWebModule {
  val title = "GMP Properties"
  val order = 4

  val layout = new FormLayout

  override def buildTabContent(app: Application) = {
    val topLabel = new Label()
    topLabel.setCaption("Property name")
    topLabel.setValue("Property value")
    layout.addComponent(topLabel)

    GmpProperties.values() foreach {
      p =>
        val label = new Label()
        label.setCaption(p.name)
        label.setValue(propertyHolder.getProperty(p.name))
        layout.addComponent(label)
    }

    layout
  }

}