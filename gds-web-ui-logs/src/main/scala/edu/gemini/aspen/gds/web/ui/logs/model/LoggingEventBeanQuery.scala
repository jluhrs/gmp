package edu.gemini.aspen.gds.web.ui.logs.model

import scala.collection.JavaConversions._
import java.util.List
import org.ops4j.pax.logging.spi.PaxLoggingEvent
import org.vaadin.addons.lazyquerycontainer._

class LoggingEventBeanQuery(queryDefinition: QueryDefinition, queryConfiguration: java.util.Map[String, Object], sortPropertyIds: Array[Object], sortStates: Array[Boolean]) extends AbstractBeanQuery[PaxLoggingEvent](queryDefinition, queryConfiguration, sortPropertyIds, sortStates) {
  val logSource = queryDefinition match {
    case s: LogSourceQueryDefinition => s.logSource
    case _ => error("Should not happen")
  }
  def saveBeans(p1: List[PaxLoggingEvent], p2: List[PaxLoggingEvent], p3: List[PaxLoggingEvent]) {}

  def loadBeans(startIndex: Int, count: Int) = {
    logSource.logEvents.takeRight(count).toList
  }

  def size() = logSource.logEvents.size

  def constructBean() = null
}



