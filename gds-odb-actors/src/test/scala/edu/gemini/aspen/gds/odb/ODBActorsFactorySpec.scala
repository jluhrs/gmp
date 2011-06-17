package edu.gemini.aspen.gds.odb

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.aspen.gds.keywords.database.{StoreProgramId, ProgramIdDatabaseImpl}
import edu.gemini.aspen.giapi.data.{ObservationEvent, FitsKeyword, DataLabel}

@RunWith(classOf[JUnitRunner])
class ODBActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
  val databaseService = mock[IDBDatabaseService]
  val programIDDatabase = new ProgramIdDatabaseImpl()
  val programID = "GS-2011-Q-56"
  val dataLabel = new DataLabel("GS-2011")

  programIDDatabase ! StoreProgramId(dataLabel, programID)

  describe("An ODBActorsFactory") {
    it("should return an empty list of Actors for start acquisition") {
      val (dataLabel, odbActorsFactory) = createFixture

      val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
      actors should be('empty)
    }
    it("should return an empty list of Actors for end acquisition") {
      val (dataLabel, odbActorsFactory) = createFixture

      val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
      actors should be('empty)
    }
    it("should be configurable with one item") {
      val (dataLabel, odbActorsFactory) = createFixture
      val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") :: Nil
      odbActorsFactory.configure(configuration)

      val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
      actors should have length (1)
    }
    it("should be configurable with two item") {
      val (dataLabel, odbActorsFactory) = createFixture

      val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
        buildOneConfiguration("OBS_START_ACQ", "STATUS2", "gpi:status2") :: Nil
      odbActorsFactory.configure(configuration)

      val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
      actors should have length (1)
    }
    it("should ignore unknown datalabels") {
      val (dataLabel, odbActorsFactory) = createFixture

      val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
      actors should be('empty)
    }
  }

  def createFixture = (
    dataLabel,
    new ODBActorsFactory(databaseService, programIDDatabase))

  def buildOneConfiguration(event: String, keyword: String, channel: String): GDSConfiguration = {
    GDSConfiguration(Instrument("GPI"), GDSEvent(event), new FitsKeyword(keyword), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("ODB"), Channel(channel), ArrayIndex(0), FitsComment("A comment"))
  }

  def buildOneNonStatusConfiguration(event: String, keyword: String, channel: String): GDSConfiguration = {
    GDSConfiguration(Instrument("GPI"), GDSEvent(event), new FitsKeyword(keyword), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("STATUS"), Channel(channel), ArrayIndex(0), FitsComment("A comment"))
  }

}