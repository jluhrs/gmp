package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import com.vaadin.data.util.ObjectProperty
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate}
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusDatabaseService}
import com.vaadin.ui.Label

@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class CurrentStatusPanel(@Requires statusDB: StatusDatabaseService) extends AbstractStatusPanelModule {
  override val order = 0
  val label = "Status:"
  val item = "Running"
  val property = new ObjectProperty[String](getStatus)
  itemValue.setContentMode(Label.CONTENT_XHTML)

  override def refresh {
    property.setValue(getStatus)
  }

  private def getStatus = {
    statusDB.getStatusItem("gpi:gds:health") match {
      case x: StatusItem[_] => if (x.getValue == Health.GOOD) {
        "<span style=\"color: green\">" + x.getValue.toString + "</span>"
      } else
      if (x.getValue == Health.WARNING) {
        "<span style=\"color: orange\">" + x.getValue.toString + "</span>"
      } else
      if (x.getValue == Health.BAD) {
        "<span style=\"color: red\">" + x.getValue.toString + "</span>"
      } else {
        x.getValue.toString
      }
      case _ => "Unknown"
    }
  }
}