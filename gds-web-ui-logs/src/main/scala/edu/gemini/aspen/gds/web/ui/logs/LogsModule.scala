package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import com.vaadin.Application
import com.vaadin.ui.{VerticalLayout, Table, Component}
import scala.collection.JavaConversions._
import com.vaadin.data.util.{BeanItemContainer, IndexedContainer}
import org.ops4j.pax.logging.spi.PaxLoggingEvent
import org.vaadin.addons.lazyquerycontainer.{LazyQueryContainer, BeanQueryFactory}

class LogsModule(logSource:LogSource) extends GDSWebModule {
  val title: String = "Logs"
  val order: Int = 4
  val logTable = new Table()
  val model = new LogsContainer(logSource)

  override def buildTabContent(app: Application): Component = {
    logTable.setSizeFull()

   // val queryFactory = new BeanQueryFactory[PaxLoggingEvent](classOf[PaxLoggingEvent])
    //val queryView = new BeanQueryView[PaxLoggingEvent](classOf[PaxLoggingEvent])

//    val queryConfiguration= Map("taskService" -> "Sh");
  //  queryFactory.setQueryConfiguration(queryConfiguration);

    //val container=new LazyQueryContainer(queryFactory,50);
    //logTable.setContainerDataSource(container)

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

class LogsContainer(logSource:LogSource) extends BeanItemContainer[PaxLoggingEvent](classOf[PaxLoggingEvent]) {
  /*addContainerProperty("TIMESTAMP", classOf[Long], 0)
  addContainerProperty("COMPONENT", classOf[String], "")
  addContainerProperty("MESSAGE", classOf[String], "")
  addContainerProperty("ERROR", classOf[String], "")*/
}

object LogsModule {

}