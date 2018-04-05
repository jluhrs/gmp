package edu.gemini.aspen.gds.properties

import org.junit.Assert._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PropertiesActorsFactoryTest extends FunSuite {
  val programID = "GS-2011-Q-56"
  val dataLabel = new DataLabel("GS-2011")
  val propertiesFactory = new PropertiesActorsFactory()

  test("One configurable item") {
    val configuration = buildOneConfiguration("OBS_PREP", "JAVAVER", "java.runtime.version")
    propertiesFactory.configure(configuration)

    val actors = propertiesFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
    assertEquals(1, actors.length)
  }

  test("Without a configurable item") {
    val configuration = buildOneNonPropertyConfiguration("OBS_PREP", "JAVAVER", "java.runtime.version")
    propertiesFactory.configure(configuration)

    val actors = propertiesFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
    assertTrue(actors.isEmpty)
  }

  def buildOneConfiguration(event: String, keyword: String, channel: String) =
    List(GDSConfiguration("GPI", event, keyword, 0, "STRING", false, "NONE", "PROPERTY", channel, 0, "", "A comment"))

  def buildOneNonPropertyConfiguration(event: String, keyword: String, channel: String) =
    List(GDSConfiguration("GPI", event, keyword, 0, "STRING", false, "NONE", "ODB", channel, 0, "", "A comment"))

}