package edu.gemini.aspen.gds.web.ui.logs.model

import scala.collection.JavaConversions._
import java.util.List
import org.ops4j.pax.logging.spi.PaxLoggingEvent
import org.vaadin.addons.lazyquerycontainer._
import java.lang.UnsupportedOperationException
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
class LoggingEventBeanQuery(queryDefinition: QueryDefinition, queryConfiguration: java.util.Map[String, Object], sortPropertyIds: Array[Object], sortStates: Array[Boolean]) extends AbstractBeanQuery[PaxLoggingEvent](queryDefinition, queryConfiguration, sortPropertyIds, sortStates) {
  val logSource = queryDefinition match {
    case s: LogSourceQueryDefinition => s.logSource
    case _ => error("Should not happen")
  }

  def saveBeans(p1: List[PaxLoggingEvent], p2: List[PaxLoggingEvent], p3: List[PaxLoggingEvent]) {
    throw new UnsupportedOperationException()
  }

  def loadBeans(startIndex: Int, count: Int) = {
    logSource.logEvents.takeRight(count).toList
  }

  def size() = logSource.logEvents.size

  def constructBean() = throw new UnsupportedOperationException()

}

object LoggingEventBeanQuery {
  val timeStampFormatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)

  def formatTimeStamp(timeStamp: Long) = timeStampFormatter.print(timeStamp)

  def formatLoggerName(loggerName: String) = if (loggerName.contains(".")) loggerName.substring(loggerName.lastIndexOf(".")+1 , loggerName.size) else loggerName

}