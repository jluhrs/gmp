package edu.gemini.aspen.gmp.web.ui.status

import com.vaadin.Application
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.VerticalLayout
import edu.gemini.aspen.giapi.web.ui.vaadin.selects.Table
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.containers.{Panel, Window}
import edu.gemini.aspen.giapi.web.ui.vaadin.components.Label
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import org.apache.felix.ipojo.annotations.{Requires, Component}
import com.vaadin.data.Container
import com.vaadin.data.util.IndexedContainer
import scala.collection.JavaConversions._
import edu.gemini.aspen.giapi.web.ui.vaadin.data.{Property, Item}

@Component(name = "GMPStatusApp")
class GMPStatusApp(@Requires statusDB:StatusDatabaseService) extends Application {
  def init() {
    setMainWindow(new Window(caption = "GMP Status Items") {
      add(new VerticalLayout(sizeFull = true, width = 100 percent, height = 100 percent) {
        add(new Panel() {
          add(new Label("GMP Status Items"))
        })
        add(new Table(width = 100 percent, height = 100 percent, dataSource = buildDataSource))
      })
    })
  }

  private def buildDataSource:Container = {
    val c = new IndexedContainer()
    c.addContainerProperty("name", classOf[String], "")
    c.addContainerProperty("value", classOf[String], "")
    c.addContainerProperty("timestamp", classOf[String], "")
    var i = 0
    statusDB.getAll foreach {
      s => val it = c.addItem(i)
      i=i+1
      it.getItemProperty("name").setValue(Property(s.getName))
      it.getItemProperty("value").setValue(Property(s.getValue))
      it.getItemProperty("timestamp").setValue(Property(s.getTimestamp))
    }
    c
  }
}