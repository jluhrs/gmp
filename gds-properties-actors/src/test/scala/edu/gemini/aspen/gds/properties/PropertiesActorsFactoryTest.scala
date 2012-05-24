package edu.gemini.aspen.gds.properties

import org.junit.Assert._
import org.junit.Test
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.seqexec.PropertiesActorsFactory

class PropertiesActorsFactoryTest extends Mockito {
  val programID = "GS-2011-Q-56"
  val dataLabel = new DataLabel("GS-2011")
  val propertiesFactory = new PropertiesActorsFactory()

  @Test
  def testConfigureWithOneItem {
    val configuration = buildOneConfiguration("OBS_PREP", "JAVAVER", "java.runtime.version")
    propertiesFactory.configure(configuration)

    val actors = propertiesFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
    assertEquals(0, actors.length)
  }

  def buildOneConfiguration(event: String, keyword: String, channel: String) =
    List(GDSConfiguration("GPI", event, keyword, 0, "STRING", false, "NONE", "PROPERTY", channel, 0, "A comment"))

  def buildOneNonPropesConfiguration(event: String, keyword: String, channel: String) =
    List(GDSConfiguration("GPI", event, keyword, 0, "STRING", false, "NONE", "ODB", channel, 0, "A comment"))

}