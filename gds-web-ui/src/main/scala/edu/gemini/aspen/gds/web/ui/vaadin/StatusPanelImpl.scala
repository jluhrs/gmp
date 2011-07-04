package edu.gemini.aspen.gds.web.ui.vaadin

import com.vaadin.ui._
import org.apache.felix.ipojo.annotations.{Unbind, Bind, Provides, Instantiate}
import java.util.logging.Logger
import edu.gemini.aspen.gds.web.ui.api.{StatusPanelModule, StatusPanel}

/**
 * Component that con display a status panel composing status panel modules
 */
@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanel]))
class StatusPanelImpl extends StatusPanel {
  private val LOG = Logger.getLogger(this.getClass.getName)
  var modules = List[StatusPanelModule]()

  override def buildStatusPanel = {
    val layout = new HorizontalLayout
    layout.setMargin(false)
    layout.setStyleName("gds-status")

    modules foreach {
      m =>
        val statusItem = m.buildModule
        layout.addComponent(statusItem)
        layout.setComponentAlignment(statusItem, Alignment.MIDDLE_RIGHT)
    }

    layout.setHeight("30px")
    layout.setWidth("100%")

    new Panel(layout)
  }

  @Bind(optional = true, aggregate = true)
  def bindStatusPanelModule(module: StatusPanelModule) {
    LOG.info("StatusPanel> status module detected " + module)
    modules = module :: modules
  }

  @Unbind
  def unbindModule(module: StatusPanelModule) {
    LOG.info("StatusPanel> status module gone " + module)
  }
}