package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.ui.{VerticalLayout, Table, Component}
import model.{LogSourceQueryDefinition, LoggingEventBeanQuery}
import scala.collection.JavaConversions._
import org.vaadin.addons.lazyquerycontainer._

class LogsModule(logSource: LogSource) extends GDSWebModule {
  val title: String = "Logs"
  val order: Int = 4
  val logTable = new Table()

  override def buildTabContent(app: Application): Component = {
    logTable.setSizeFull()

    val queryFactory = new BeanQueryFactory[LoggingEventBeanQuery](classOf[LoggingEventBeanQuery])
    val definition = new LogSourceQueryDefinition(logSource, false, 50)

    //definition.
    definition.addProperty("timeStamp", classOf[java.lang.Long], 0L, true, true)
    definition.addProperty("message", classOf[String], "DEFAULT", true, true)
    //definition.addProperty("Reverse Index", classOf[Integer], 0, true, false)
    //definition.addProperty("Editable", classOf[String], "", false, false)
    //definition.addProperty(LazyQueryView.PROPERTY_ID_ITEM_STATUS, classOf[QueryItemStatus], QueryItemStatus.None, true, false)
    //definition.addProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_INDEX, classOf[Int], 0, true, false)
    //definition.addProperty(LazyQueryView.DEBUG_PROPERTY_ID_BATCH_QUERY_TIME, classOf[Long], 0, true, false)
    //definition.addProperty(LazyQueryView.DEBUG_PROPERTY_ID_QUERY_INDEX, classOf[Int], 0, true, false)
    //    val queryConfiguration= Map("taskService" -> "Sh");
    //queryFactory.setQueryConfiguration(definition);
    queryFactory.setQueryDefinition(definition);

    val container = new LazyQueryContainer(definition, queryFactory);
    logTable.setContainerDataSource(container)

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



