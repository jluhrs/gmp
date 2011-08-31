package edu.gemini.aspen.gds.web.ui.logs

import org.junit.Test
import org.junit.Assert._
import java.security.SecureRandom
import org.ops4j.pax.logging.service.internal.PaxLoggingEventImpl
import org.apache.log4j.spi.LoggingEvent
import org.apache.log4j.{Level, Logger}

/**
 * Tests for the InMemoryLogSource class
 */
class InMemoryLogSourceTest {
  @Test
  def testBuild {
    val appender = new InMemoryLogSource()
    assertNotNull(appender)
  }

  @Test
  def testAppend {
    val appender = new InMemoryLogSource()
    val event = newLoggingEvent
    appender.doAppend(event)
    assertEquals(1, appender.logEvents.size)
  }

  val rnd = new SecureRandom()
  val category = Logger.getLogger("Logger")
  val level = Level.INFO
  def newLoggingEvent = {
    val e = new LoggingEvent("myclass", category, level, "msg", null)
    new PaxLoggingEventImpl(e)
  }
}