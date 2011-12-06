package edu.gemini.aspen.gds.web.ui.logs.model

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.ops4j.pax.logging.service.internal.PaxLevelImpl
import org.apache.log4j.Level
import edu.gemini.aspen.gds.web.ui.logs.InMemoryLogSource
import com.vaadin.data.util.filter.{Compare, And}
import com.vaadin.data.util.{ObjectProperty, PropertysetItem}

@RunWith(classOf[JUnitRunner])
class LogSourceQueryDefinitionTest extends FunSuite {
  test("test adding filter") {
    val logSource =  new InMemoryLogSource
    val queryDefinition = new LogSourceQueryDefinition(logSource, false, 10)

    val logEvent = LogEventWrapper(new PaxLevelImpl(Level.DEBUG), System.currentTimeMillis, "unknown", "message", null)
    val logs = List(logEvent)

    assertEquals(logs, queryDefinition.filterResults(logs))

    // Add a filter that passes
    val f = new And(new Compare.Equal("level", "DEBUG"), new Compare.Equal("logger", "unknown"))

    val item = new PropertysetItem
    item.addItemProperty("level", new ObjectProperty[String]("DEBUG"))
    item.addItemProperty("logger", new ObjectProperty[String]("unknown"))
    assertTrue(f.passesFilter(1, item))

    val item2 = new PropertysetItem
    item2.addItemProperty("level", new ObjectProperty[String]("DEBUG"))
    assertFalse(f.passesFilter(1, item2))

    queryDefinition.addContainerFilter(f)
    //assertEquals(logs, queryDefinition.filterResults(logs))
  }
}