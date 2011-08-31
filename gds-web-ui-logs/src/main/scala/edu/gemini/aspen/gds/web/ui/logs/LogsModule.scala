package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.ui.{Panel, Component}

class LogsModule extends GDSWebModule {
  val title: String = "Logs"
  val order: Int = 4

  override def buildTabContent(app: Application): Component = {
    new Panel()
  }

  override def refresh() {
  }

}

object LogsModule {

}