package edu.gemini.aspen.gds.web.ui.logs.model

import reflect.BeanProperty
import org.ops4j.pax.logging.spi.{PaxLevel, PaxLoggingEvent}

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
case class LogEventWrapper(level0: PaxLevel, timeStamp0: Long, message0: String, loggerName0: String, throwableStrRep0:Array[String]) {
  val throwableStrRep = Option(throwableStrRep0) getOrElse (Array[String]())
  def this(event: PaxLoggingEvent) {
    this (event.getLevel, event.getTimeStamp, event.getMessage, event.getLoggerName, event.getThrowableStrRep)
  }

  @BeanProperty val level = level0.toString
  @BeanProperty val message = LoggingEventBeanQuery.formatMessage(message0)
  @BeanProperty val timeStamp = LoggingEventBeanQuery.formatTimeStamp(timeStamp0)
  @BeanProperty val loggerName = LoggingEventBeanQuery.formatLoggerName(loggerName0)
  @BeanProperty val stackTrace = throwableStrRep.mkString
}



