package edu.gemini.aspen.gds.web.ui.vaadin

import java.util.logging.Logger
import org.apache.felix.ipojo.annotations.{Unbind, Bind, Provides, Instantiate}
import edu.gemini.aspen.gds.web.ui.api.{StatusPanelModule, StatusPanel}
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Label
import com.vaadin.ui.{Panel, Alignment}

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
    val layout = new HorizontalLayout(margin = false, style = "gds-status", height = 25 px, width = 100 percent)

    modules sortBy {
      _.order
    } foreach {
      m =>
        val statusItem = m.buildModule
        layout.add(statusItem, alignment = Alignment.MIDDLE_CENTER, ratio = 1f / modules.size)
        layout.add(new Label(style = "vertical-separator") {
          setHeight(100 percent)
          setWidth(1 px)
        })
    }
    new Panel(layout)
  }


  def refresh {
    modules foreach {
      m => m.refresh
    }
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