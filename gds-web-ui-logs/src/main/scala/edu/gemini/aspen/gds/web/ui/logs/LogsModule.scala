package edu.gemini.aspen.gds.web.ui.logs

import java.util.logging.Logger
import model.{LogsContainer, LogSourceQueryDefinition, LoggingEventBeanQuery}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import edu.gemini.aspen.giapi.web.ui.vaadin.layouts._
import edu.gemini.aspen.giapi.web.ui.vaadin.selects._
import com.vaadin.terminal.ThemeResource
import com.vaadin.Application
import com.vaadin.ui.Component
import org.vaadin.addons.lazyquerycontainer._

/**
 * The LogsModule class shows the content of GDS related log files */
class LogsModule(logSource: LogSource) extends GDSWebModule {
  val LOG = Logger.getLogger(this.getClass.getName)
  val STYLES = Map("WARN" -> "warn", "ERROR" -> "error")

  val title: String = "Logs"
  val order: Int = 1

  //val expandIcon = new ThemeResource("../runo/icons/16/arrow-right.png")
  //val expandTooltip = "Show stack trace"
  val dataContainer = buildDataContainer()
  val logTable = new Table(dataSource = dataContainer,
    selectable = true,
    style = "logs",
    sizeFull = true,
    sortAscending = true,
    sortPropertyId = "timeStamp",
    cellStyleGenerator = styleGenerator)

  override def buildTabContent(app: Application): Component = new VerticalLayout(sizeFull = true) {
    add(logTable, ratio = 1.0f)
  }

  override def refresh(app: Application) {
    dataContainer.refresh()
  }

  private def buildDataContainer() = {
    val queryFactory = new BeanQueryFactory[LoggingEventBeanQuery](classOf[LoggingEventBeanQuery])
    val definition = new LogSourceQueryDefinition(logSource, false, 300)

    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("level", classOf[String], "", true, true)
    definition.addProperty("loggerName", classOf[String], "", true, true)
    definition.addProperty("message", classOf[String], "", true, true)
    queryFactory.setQueryDefinition(definition);

    new LogsContainer(definition, queryFactory)
  }
  /**
   * Define a custom cell style based on the content of the cell */
  private def styleGenerator(itemId: AnyRef, propertyId: AnyRef): String = {
    STYLES.getOrElse(dataContainer.getItem(itemId).getItemProperty("level").toString, "")
  }
}