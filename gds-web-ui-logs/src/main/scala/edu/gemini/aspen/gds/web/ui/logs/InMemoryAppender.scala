package edu.gemini.aspen.gds.web.ui.logs

import org.apache.felix.ipojo.annotations._
import org.ops4j.pax.logging.spi.{PaxLoggingEvent, PaxAppender}

/**
 * A PaxAppender service thatwill get log events from pax-logging */
@Component
@Instantiate
@Provides(specifications = Array(classOf[PaxAppender]))
class InMemoryAppender extends PaxAppender {
  /** Service property that matches the configuration on the org.ops4j.pax.logging.cfg file */
  @ServiceProperty(name = "org.ops4j.pax.logging.appender.name", value = "GeminiAppender")
  val name = "GeminiAppender"

  @Validate
  def initLogListener() {
  }

  def doAppend(event: PaxLoggingEvent) {
  }
}