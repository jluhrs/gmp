package edu.gemini.aspen.gds.web.ui.logs

import scala.collection.JavaConversions._
import java.util.logging.Logger
import model.{LogsContainer, LogSourceQueryDefinition, LoggingEventBeanQuery}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import com.vaadin.terminal.ThemeResource
import com.vaadin.Application
import com.vaadin.ui.{Component, Table}
import com.vaadin.ui.Table.CellStyleGenerator
import org.vaadin.addons.lazyquerycontainer._

/**
 * The LogsModule class shows the content of GDS related log files */
class LogsModule(logSource: LogSource) extends GDSWebModule {
  val LOG = Logger.getLogger(this.getClass.getName)
  val title: String = "Logs"
  val order: Int = 1
  val logTable = new Table()
  val styleGenerator = new CellStyleGenerator {
    val styles = Map("WARN" -> "warn", "ERROR" -> "error")

    def getStyle(itemId: AnyRef, propertyId: AnyRef) = {
      styles.getOrElse(container.getItem(itemId).getItemProperty("level").toString, "")
    }
  }
  val expandIcon = new ThemeResource("../runo/icons/16/arrow-right.png")
  val expandTooltip = "Show stack trace"
  val container = {
    val queryFactory = new BeanQueryFactory[LoggingEventBeanQuery](classOf[LoggingEventBeanQuery])
    val definition = new LogSourceQueryDefinition(logSource, false, 300)

    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("level", classOf[String], "", true, true)
    definition.addProperty("loggerName", classOf[String], "", true, true)
    definition.addProperty("message", classOf[String], "", true, true)
    queryFactory.setQueryDefinition(definition);

    new LogsContainer(definition, queryFactory)
  }

  override def buildTabContent(app: Application): Component = {
    logTable.setContainerDataSource(container)
    logTable.setSelectable(true)
    logTable.setSizeFull()
    logTable.addStyleName("logs")
    logTable.setColumnReorderingAllowed(true)
    logTable.setSortContainerPropertyId("timeStamp")
    logTable.setSortAscending(false)

    logTable.setCellStyleGenerator(styleGenerator)

    new VerticalLayout(sizeFull=true) {
      add(logTable, ratio=1.0f)
    }
  }

  override def refresh(app: Application) {
    container.refresh()
  }
}
