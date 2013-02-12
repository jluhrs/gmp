package edu.gemini.aspen.gds.status

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.junit.Assert._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import fits.FitsKeyword
import edu.gemini.aspen.gds.api.Conversions._
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class InstrumentStatusActorsFactoryTest extends FunSuite with ShouldMatchers with MockitoSugar {
  val statusDB = mock[StatusDatabaseService]
  val dataLabel = new DataLabel("GS-2011")
  val instrumentStatusActorsFactory = new InstrumentStatusActorsFactory(statusDB)

  test("should return an empty list of Actors when not configured") {
    val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertTrue(actors.isEmpty)
  }
  test("should be configurable with one item") {
    val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") :: Nil
    instrumentStatusActorsFactory.configure(configuration)

    val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(actors.length, 1)
  }
  test("should be configurable with two item") {
    val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
      buildOneConfiguration("OBS_START_ACQ", "STATUS2", "gpi:status2") :: Nil
    instrumentStatusActorsFactory.configure(configuration)

    val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(actors.length, 2)
  }
  test("should be configurable with one item for start and one item for end") {
    val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
      buildOneConfiguration("OBS_END_ACQ", "STATUS2", "gpi:status2") :: Nil
    instrumentStatusActorsFactory.configure(configuration)

    val startActors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(startActors.length, 1)
    val endActors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
    assertEquals(endActors.length, 1)
  }
  test("should only pick Instrument Status subsystems") {
    val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
      buildOneNonStatusConfiguration("OBS_END_ACQ", "STATUS2", "gpi:status2") :: Nil
    instrumentStatusActorsFactory.configure(configuration)

    val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(actors.length, 1)
  }

  def buildOneConfiguration(event: String, keyword: String, channel: String): GDSConfiguration = {
    GDSConfiguration(
      Instrument("GPI"),
      GDSEvent(event),
      FitsKeyword(keyword),
      HeaderIndex(0),
      DataType("DOUBLE"),
      Mandatory(false),
      DefaultValue("NONE"),
      Subsystem(KeywordSource.STATUS),
      Channel(channel),
      ArrayIndex(0),
      "",
      FitsComment("A comment"))
  }

  def buildOneNonStatusConfiguration(event: String, keyword: String, channel: String): GDSConfiguration = {
    GDSConfiguration(
      Instrument("GPI"),
      GDSEvent(event),
      FitsKeyword(keyword),
      HeaderIndex(0),
      DataType("DOUBLE"),
      Mandatory(false),
      DefaultValue("NONE"),
      Subsystem(KeywordSource.EPICS),
      Channel(channel),
      ArrayIndex(0),
      "",
      FitsComment("A comment"))
  }

}