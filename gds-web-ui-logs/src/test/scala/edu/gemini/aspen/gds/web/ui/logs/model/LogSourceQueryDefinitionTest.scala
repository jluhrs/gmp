package edu.gemini.aspen.gds.web.ui.logs.model

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.ops4j.pax.logging.service.internal.PaxLevelImpl
import org.apache.log4j.Level
import edu.gemini.aspen.gds.web.ui.logs.InMemoryLogSource
import edu.gemini.aspen.giapi.web.ui.vaadin.data._
import com.vaadin.data.util.filter.{Compare, And}

@RunWith(classOf[JUnitRunner])
class LogSourceQueryDefinitionTest extends FunSuite {
  test("test raw filters") {
    // Add a filter that passes
    val f = new And(new Compare.Equal("level", "DEBUG"), new Compare.Equal("logger", "unknown"))

    val item = Item("level" -> "DEBUG", "logger" -> "unknown")
    assertTrue(f.passesFilter(1, item))
    assertTrue(f.appliesToProperty("level"))
    assertTrue(f.appliesToProperty("logger"))

    val item2 = Item("level" -> "DEBUG")
    assertFalse(f.passesFilter(1, item2))
  }
  
  test("test adding filter") {
    val logSource =  new InMemoryLogSource
    val queryDefinition = new LogSourceQueryDefinition(logSource, false, 10)

    val logEvent = LogEventWrapper(new PaxLevelImpl(Level.DEBUG), System.currentTimeMillis, "unknown", "message", null)
    val logs = List(logEvent)

    //assertEquals(logs, queryDefinition.filterResults(logs))

    // Add a filter that passes
    val f = new Compare.Equal("level", "DEBUG")
    assertEquals(logs, queryDefinition.filter(f, logs))

    queryDefinition.addContainerFilter(f)
  }
}