package edu.gemini.aspen.gds.web.ui.logs.model

import scala.collection.JavaConversions._
import java.util.List
import java.util.logging.Logger
import java.lang.UnsupportedOperationException
import org.vaadin.addons.lazyquerycontainer._
import org.joda.time.format.ISODateTimeFormat
import org.joda.time.DateTimeZone

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
class LoggingEventBeanQuery(queryDefinition0: QueryDefinition, queryConfiguration: java.util.Map[String, Object], sortPropertyIds: Array[Object], sortStates: Array[Boolean]) extends AbstractBeanQuery[LogEventWrapper](queryDefinition0, queryConfiguration, sortPropertyIds, sortStates) {
  val LOG = Logger.getLogger(this.getClass.getName)

  val logSource:LogSourceQueryDefinition = queryDefinition0 match {
    case q: LogSourceQueryDefinition =>
      val ls = new LogSourceQueryDefinition(q.logSource, true, 200)
      q.filters foreach {
        case f => ls.addContainerFilter(f)
      }
      ls
    case _ => sys.error("Should not happen")
  }

  override def saveBeans(p1: List[LogEventWrapper], p2: List[LogEventWrapper], p3: List[LogEventWrapper]) {
    throw new UnsupportedOperationException()
  }

  override def loadBeans(startIndex: Int, count: Int) = {
    val result = filteredLogs drop(startIndex - 1) take(count) toList

    val sortProperties = sortPropertyIds.headOption.getOrElse("timeStamp").toString
    val ascending = sortStates.headOption.getOrElse(true)

    val sortedLog = result sortBy LoggingEventBeanQuery.sortingFunctions(sortProperties)

    if (ascending) {
      sortedLog
    } else {
      sortedLog reverse
    }
  }

  override def size(): Int = Option(filteredLogs).getOrElse(Nil).size

  override def constructBean() = throw new UnsupportedOperationException()

  private def filteredLogs = logSource.filterResults(logSource.logSource.logEvents)
}

object LoggingEventBeanQuery {
  val timeStampFormatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)
  val MAX_MESSAGE_LENGTH = 150
  /**Functions used to sort the logs based on different properties */
  val sortingFunctions = Map[String, (LogEventWrapper) => String](
    "timeStamp" -> {
      _.timeStamp
    },
    "level" -> {
      _.level.toString
    },
    "loggerName" -> {
      _.loggerName
    },
    "message" -> {
      _.message
    }
  )

  /**Formats the timestamp output */
  def formatTimeStamp(timeStamp: Long) = timeStampFormatter.print(timeStamp)

  /**Formats the output of the logger name */
  def formatLoggerName(loggerName: String) = if (loggerName.contains(".")) loggerName.substring(loggerName.lastIndexOf(".") + 1, loggerName.size) else loggerName

  /**Formats the log message */
  def formatMessage(message: String) = if (message.size > MAX_MESSAGE_LENGTH) message.substring(0, MAX_MESSAGE_LENGTH) + "..." else message
}