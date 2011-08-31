package edu.gemini.aspen.gds.web.ui.configuration

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.ui.{Panel, Component}
import com.vaadin.Application

class ConfigurationModule extends GDSWebModule {
  val title: String = "System Configuration"
  val order: Int = 5

  override def buildTabContent(app: Application): Component = {
    new Panel()
  }

  override def refresh() {
  }

}

object ConfigurationModule {

}