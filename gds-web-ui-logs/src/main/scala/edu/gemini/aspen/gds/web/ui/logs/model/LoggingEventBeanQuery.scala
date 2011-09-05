package edu.gemini.aspen.gds.web.ui.logs.model

import scala.collection.JavaConversions._
import java.util.List
import org.vaadin.addons.lazyquerycontainer._
import java.lang.UnsupportedOperationException
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone
import java.util.logging.Logger

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
class LoggingEventBeanQuery(queryDefinition: QueryDefinition, queryConfiguration: java.util.Map[String, Object], sortPropertyIds: Array[Object], sortStates: Array[Boolean]) extends AbstractBeanQuery[LogEventWrapper](queryDefinition, queryConfiguration, sortPropertyIds, sortStates) {
  val LOG = Logger.getLogger(this.getClass.getName)
  val sortingFunctions = Map[String, (LogEventWrapper) => String](
    "timeStamp" ->  { _.timeStamp },
    "level" -> { _.level.toString },
    "loggerName" -> { _.loggerName },
    "message" -> { _.message }
  )
  
  val logSource = queryDefinition match {
    case s: LogSourceQueryDefinition => s.logSource
    case _ => error("Should not happen")
  }

  def saveBeans(p1: List[LogEventWrapper], p2: List[LogEventWrapper], p3: List[LogEventWrapper]) {
    throw new UnsupportedOperationException()
  }

  def loadBeans(startIndex: Int, count: Int) = {
    val result = logSource.logEvents.drop(startIndex - 1).take(count) toList

    val sortableCol = if (!sortPropertyIds.isEmpty) sortPropertyIds(0).toString else "timeStamp"
    val ascending = if (!sortStates.isEmpty) sortStates(0) else true

    val r = result sortBy sortingFunctions(sortableCol)

    if (!ascending) {
      r reverse
    } else {
      r
    }
  }

  def size() = logSource.logEvents.size

  def constructBean() = throw new UnsupportedOperationException()

}

object LoggingEventBeanQuery {
  val timeStampFormatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)
  val MAX_MESSAGE_LENGTH = 100

  def formatTimeStamp(timeStamp: Long) = timeStampFormatter.print(timeStamp)

  def formatLoggerName(loggerName: String) = if (loggerName.contains(".")) loggerName.substring(loggerName.lastIndexOf(".") + 1, loggerName.size) else loggerName

  def formatMessage(message: String) = if (message.size > MAX_MESSAGE_LENGTH) message.substring(0, MAX_MESSAGE_LENGTH) + "..." else message

}