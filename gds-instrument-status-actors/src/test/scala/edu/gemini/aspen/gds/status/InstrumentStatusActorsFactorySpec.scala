package edu.gemini.aspen.gds.status

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import fits.FitsKeyword
import edu.gemini.aspen.gds.api.Conversions._

@RunWith(classOf[JUnitRunner])
class InstrumentStatusActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
  val statusDB = mock[StatusDatabaseService]
  val dataLabel = new DataLabel("GS-2011")
  val instrumentStatusActorsFactory = new InstrumentStatusActorsFactory(statusDB)

  describe("An InstrumentStatusActorsFactory") {
    it("should return an empty list of Actors when not configured") {
      val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should be('empty)
    }
    it("should be configurable with one item") {
      val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") :: Nil
      instrumentStatusActorsFactory.configure(configuration)

      val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should have length (1)
    }
    it("should be configurable with two item") {
      val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
        buildOneConfiguration("OBS_START_ACQ", "STATUS2", "gpi:status2") :: Nil
      instrumentStatusActorsFactory.configure(configuration)

      val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should have length (2)
    }
    it("should be configurable with one item for start and one item for end") {
      val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
        buildOneConfiguration("OBS_END_ACQ", "STATUS2", "gpi:status2") :: Nil
      instrumentStatusActorsFactory.configure(configuration)

      val startActors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      startActors should have length (1)
      val endActors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
      endActors should have length (1)
    }
    it("should only pick Instrument Status subsystems") {
      val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
        buildOneNonStatusConfiguration("OBS_END_ACQ", "STATUS2", "gpi:status2") :: Nil
      instrumentStatusActorsFactory.configure(configuration)

      val actors = instrumentStatusActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should have length (1)
    }
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