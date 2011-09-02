package edu.gemini.aspen.gds.web.ui.logs.model

import scala.collection.JavaConversions._
import java.util.List
import org.ops4j.pax.logging.spi.PaxLoggingEvent
import org.vaadin.addons.lazyquerycontainer._
import java.lang.UnsupportedOperationException
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone
import reflect.BeanProperty

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
class LoggingEventBeanQuery(queryDefinition: QueryDefinition, queryConfiguration: java.util.Map[String, Object], sortPropertyIds: Array[Object], sortStates: Array[Boolean]) extends AbstractBeanQuery[LogEventWrapper](queryDefinition, queryConfiguration, sortPropertyIds, sortStates) {
  val logSource = queryDefinition match {
    case s: LogSourceQueryDefinition => s.logSource
    case _ => error("Should not happen")
  }
  println(queryDefinition.getSortablePropertyIds)

  def saveBeans(p1: List[LogEventWrapper], p2: List[LogEventWrapper], p3: List[LogEventWrapper]) {
    throw new UnsupportedOperationException()
  }

  def loadBeans(startIndex: Int, count: Int) = {
    println(queryDefinition.getSortablePropertyIds)
    println(queryDefinition.getSortablePropertyIds)
    logSource.logEvents.takeRight(count) map  {
      new LogEventWrapper(_)
    } toList
  }

  def size() = logSource.logEvents.size

  def constructBean() = throw new UnsupportedOperationException()

}

class LogEventWrapper(event:PaxLoggingEvent) {
  @BeanProperty val level = event.getLevel
  @BeanProperty val message = LoggingEventBeanQuery.formatMessage(event.getMessage)
  @BeanProperty val timeStamp = LoggingEventBeanQuery.formatTimeStamp(event.getTimeStamp)
  @BeanProperty val loggerName = LoggingEventBeanQuery.formatLoggerName(event.getLoggerName)
}

object LoggingEventBeanQuery {
  val timeStampFormatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)
  val MAX_MESSAGE_LENGTH = 100

  def formatTimeStamp(timeStamp: Long) = timeStampFormatter.print(timeStamp)

  def formatLoggerName(loggerName: String) = if (loggerName.contains(".")) loggerName.substring(loggerName.lastIndexOf(".")+1 , loggerName.size) else loggerName

  def formatMessage(message: String) = if (message.size > MAX_MESSAGE_LENGTH) message.substring(0, MAX_MESSAGE_LENGTH)+ "..." else message

}