package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import model.{LogSourceQueryDefinition, LoggingEventBeanQuery}
import scala.collection.JavaConversions._
import org.vaadin.addons.lazyquerycontainer._
import com.vaadin.ui.{Label, VerticalLayout, Table, Component}
import com.vaadin.data.util.BeanItem


class LogsModule(logSource: LogSource) extends GDSWebModule {
  val title: String = "Logs"
  val order: Int = -1
  val logTable = new Table()

  override def buildTabContent(app: Application): Component = {

    val queryFactory = new BeanQueryFactory[LoggingEventBeanQuery](classOf[LoggingEventBeanQuery])
    val definition = new LogSourceQueryDefinition(logSource, false, 50)

    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("level", classOf[String], "", true, true)
    definition.addProperty("loggerName", classOf[String], "", true, true)
    definition.addProperty("message", classOf[String], "", true, true)
    queryFactory.setQueryDefinition(definition);

    val container = new LazyQueryContainer(definition, queryFactory)
    logTable.setContainerDataSource(container)
    logTable.setSelectable(true)
    logTable.setSizeFull()
    logTable.setColumnCollapsingAllowed(true)
    logTable.setColumnReorderingAllowed(true)

    logTable.addGeneratedColumn("timeStamp", LogsModule.timeStampGenerator)
    logTable.addGeneratedColumn("loggerName", LogsModule.loggerNameGenerator)

    val layout = new VerticalLayout
    layout.setSizeFull()
    layout.addComponent(logTable)
    layout.setExpandRatio(logTable, 1.0f)
    layout
  }

  override def refresh() {
    //count.setValue(logSource.logEvents.size)
  }

}

object LogsModule {
  /**
   * Generator for the timestamp column, delegates to LoggingEventQuery the formatting of time */
  val timeStampGenerator = new Table.ColumnGenerator {
    def generateCell(table: Table, itemId: AnyRef, columnId: AnyRef) = {
      val b = table.getItem(itemId) match {
        case b: BeanItem[_] => b
        case _ => error("Should not happen ")
      }
      val timeStamp = b.getItemProperty("timeStamp").getValue match {
        case l: java.lang.Long => l
        case _ => error("Should not happen ")
      }

      new Label(LoggingEventBeanQuery.formatTimeStamp(timeStamp.longValue()))
    }
  }
  
  /**
   * Generator for the timestamp column, delegates to LoggingEventQuery the formatting of time */
  val loggerNameGenerator = new Table.ColumnGenerator {
    def generateCell(table: Table, itemId: AnyRef, columnId: AnyRef) = {
      val b = table.getItem(itemId) match {
        case b: BeanItem[_] => b
        case _ => error("Should not happen ")
      }
      val loggerName = b.getItemProperty("loggerName").getValue.toString

      new Label(LoggingEventBeanQuery.formatLoggerName(loggerName))
    }
  }

  
}