package edu.gemini.aspen.gds.epics

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.epics.{ReadOnlyClientEpicsChannel, EpicsReader}
import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class EpicsActorsFactorySpec extends FunSuite with MockitoSugar {
  val epicsReader = mock[EpicsReader]
  val channel = mock[ReadOnlyClientEpicsChannel[Double]]
  val channel2 = mock[ReadOnlyClientEpicsChannel[Double]]
  when(channel.isValid).thenReturn(true)
  when(channel2.isValid).thenReturn(true)
  //seems like a Mockito bug, the cast works around it...
  when(epicsReader.getChannelAsync("gpi:value").asInstanceOf[ReadOnlyClientEpicsChannel[Double]]).thenReturn(channel)
  when(epicsReader.getChannelAsync("gpi:value2").asInstanceOf[ReadOnlyClientEpicsChannel[Double]]).thenReturn(channel2)

  def createFixture = (
    new DataLabel("GS-2011"),
    new EpicsActorsFactory(epicsReader)
    )

  def buildOneConfiguration: GDSConfiguration = {
    GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation")
  }

  test("should return an empty list of Actors when not configured") {
    val (dataLabel, epicsActorsFactory) = createFixture

    val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertTrue(actors.isEmpty)
  }
  test("should be configurable with one item") {
    val (dataLabel, epicsActorsFactory) = createFixture
    val configuration = List(
      buildOneConfiguration
    )
    epicsActorsFactory.configure(configuration)

    val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(1, actors.size)
  }
  test("should be configurable with two item for diferent channel") {
    val (dataLabel, epicsActorsFactory) = createFixture
    val configuration = List(
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation"),
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value2", 0, "", "Mean airmass for the observation"))
    epicsActorsFactory.configure(configuration)

    val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(2, actors.size)
  }
  test("should be configurable with two item for same channel") {
    val (dataLabel, epicsActorsFactory) = createFixture
    val configuration = List(
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation"),
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation"))
    epicsActorsFactory.configure(configuration)

    val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(1, actors.size)
  }
  test("should be configurable with one item for start and one item for end") {
    val (dataLabel, epicsActorsFactory) = createFixture
    val configuration = List(
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation"),
      GDSConfiguration("GPI", "OBS_END_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation"))
    epicsActorsFactory.configure(configuration)

    val startActors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(1, startActors.size)
    val endActors = epicsActorsFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
    assertEquals(1, endActors.size)
  }
  test("should only pick EPICS subsystems") {
    val (dataLabel, epicsActorsFactory) = createFixture
    val configuration = List(
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation"),
      GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "STATUS", "gpi:value", 0, "", "Mean airmass for the observation"))
    epicsActorsFactory.configure(configuration)

    val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(1, actors.size)
  }

}