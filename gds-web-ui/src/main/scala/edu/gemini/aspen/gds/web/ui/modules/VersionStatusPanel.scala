package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component => IPOJOComponent}
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import edu.gemini.aspen.giapi.web.ui.vaadin.data._

@IPOJOComponent
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[StatusPanelModule]))
class VersionStatusPanel extends AbstractStatusPanelModule {
  override val order = 1
  val label = "Version:"
  val item = Option(System.getProperty("gmp.version")) getOrElse ("unknown")
  val property = Property[String](item)
}