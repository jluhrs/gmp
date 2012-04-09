package edu.gemini.aspen.gmp.web.ui.status

import com.vaadin.Application
import edu.gemini.aspen.giapi.web.ui.vaadin.selects.Table
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.containers.{Panel, Window}
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import org.apache.felix.ipojo.annotations.{Requires, Component}
import com.vaadin.data.Container
import com.vaadin.data.util.IndexedContainer
import scala.collection.JavaConversions._
import edu.gemini.aspen.giapi.web.ui.vaadin.data.Property
import edu.gemini.aspen.giapi.web.ui.vaadin.components.{ProgressIndicator, Label}
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler
import java.util.concurrent.{Executors, TimeUnit}
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts.{HorizontalLayout, VerticalLayout}
import java.util.Date
import com.vaadin.ui.Alignment

@Component(name = "GMPStatusApp")
class GMPStatusApp(@Requires statusDB: StatusDatabaseService) extends Application {
  val dataSource = buildDataSource
  val scheduler = Executors.newScheduledThreadPool(1)
  val lastUpdateProperty = Property("Last Update: " + new Date())

  def init() {
    val mainContent = new VerticalLayout(sizeFull = true, margin = true) {
      add(
        new HorizontalLayout(height=45 px, width = 100 percent, margin = false, style = "status-panel") {
          add(new Label("GMP Status Items", style = "status-title"), alignment = Alignment.MIDDLE_LEFT)
          add(new Label(style = "status-title", property = lastUpdateProperty), alignment = Alignment.MIDDLE_RIGHT)
        })
      add(new Table(sizeFull = true, dataSource = dataSource), ratio = 1f)
      add(new ProgressIndicator(caption = "Rate", pollingInterval = 5000, style = "status-hidden"))
    }
    setMainWindow(new Window(caption = "GMP Status Items", content = mainContent))
    setTheme("gmp")
    populateStatusList()

    scheduler.scheduleAtFixedRate(new Runnable() {
      def run() {
        populateStatusList()
      }
    }, 0, 200, TimeUnit.MILLISECONDS)
  }

  override def close() {
    scheduler.shutdown()
  }

  private def buildDataSource: Container = {
    val c = new IndexedContainer()
    c.addContainerProperty("name", classOf[String], "")
    c.addContainerProperty("value", classOf[String], "")
    c.addContainerProperty("timestamp", classOf[String], "")
    c
  }

  private def populateStatusList() {
    statusDB.getAll foreach {
      s =>
        val it = if (dataSource.containsId(s.getName)) dataSource.getItem(s.getName) else dataSource.addItem(s.getName)
        it.getItemProperty("name").setValue(Property(s.getName))
        it.getItemProperty("value").setValue(Property(s.getValue))
        it.getItemProperty("timestamp").setValue(Property(s.getTimestamp))
    }
    lastUpdateProperty.setValue("Last Update: " + new Date())
  }
}