package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import model.{LogsContainer, LogSourceQueryDefinition, LoggingEventBeanQuery}
import scala.collection.JavaConversions._
import org.vaadin.addons.lazyquerycontainer._
import java.util.logging.{Level, Logger}
import com.vaadin.terminal.ThemeResource
import com.vaadin.ui.Table.{CellStyleGenerator}
import edu.gemini.aspen.gds.web.ui.api.Preamble._
import com.vaadin.ui._

class LogsModule(logSource: LogSource) extends GDSWebModule {
  val LOG = Logger.getLogger(this.getClass.getName)
  val title: String = "Logs"
  val order: Int = 2
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
    val definition = new LogSourceQueryDefinition(logSource, false, 100)

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

    logTable.setCellStyleGenerator(styleGenerator)

    val layout = new VerticalLayout
    layout.setSizeFull()

    layout.addComponent(logTable)
    layout.setExpandRatio(logTable, 1.0f)
    layout
  }
}
