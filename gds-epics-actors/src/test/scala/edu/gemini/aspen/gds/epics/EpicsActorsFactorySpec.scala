package edu.gemini.aspen.gds.epics

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

@RunWith(classOf[JUnitRunner])
class EpicsActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
  val epicsReader = mock[EpicsReader]

  def createFixture = (
    new DataLabel("GS-2011"),
    new EpicsActorsFactory(epicsReader)
    )

  def buildOneConfiguration: GDSConfiguration = {
    GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation")
  }

  describe("An EpicsActorsFactory") {
    it("should return an empty list of Actors when not configured") {
      val (dataLabel, epicsActorsFactory) = createFixture

      val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should be('empty)
    }
    it("should be configurable with one item") {
      val (dataLabel, epicsActorsFactory) = createFixture
      val configuration = List(
        buildOneConfiguration
      )
      epicsActorsFactory.configure(configuration)

      val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should have length (1)
    }
    it("should be configurable with two item") {
      val (dataLabel, epicsActorsFactory) = createFixture
      val configuration = List(
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation"),
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation"))
      epicsActorsFactory.configure(configuration)

      val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should have length (2)
    }
    it("should be configurable with one item for start and one item for end") {
      val (dataLabel, epicsActorsFactory) = createFixture
      val configuration = List(
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation"),
        GDSConfiguration("GPI", "OBS_END_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation"))
      epicsActorsFactory.configure(configuration)

      val startActors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      startActors should have length (1)
      val endActors = epicsActorsFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
      endActors should have length (1)
    }
    it("should only pick EPICS subsystems") {
      val (dataLabel, epicsActorsFactory) = createFixture
      val configuration = List(
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation"),
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS2", 0, "DOUBLE", true, "NONE", "NONEPICS", "gpi:value", 0, "Mean airmass for the observation"))
      epicsActorsFactory.configure(configuration)

      val actors = epicsActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should have length (1)
    }
  }

}