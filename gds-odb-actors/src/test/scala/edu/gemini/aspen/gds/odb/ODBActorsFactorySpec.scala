package edu.gemini.aspen.gds.odb

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.aspen.gds.keywords.database.{StoreProgramId, ProgramIdDatabaseImpl}

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

            val actors = odbActorsFactory.buildStartAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should return an empty list of Actors for end acquisition") {
            val (dataLabel, odbActorsFactory) = createFixture

            val actors = odbActorsFactory.buildEndAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should be configurable with one item") {
            val (dataLabel, odbActorsFactory) = createFixture
            val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") :: Nil
            odbActorsFactory.configure(configuration)

            val actors = odbActorsFactory.buildPrepareObservationActors(dataLabel)
            actors should have length(1)
        }
        it("should be configurable with two item") {
            val (dataLabel, odbActorsFactory) = createFixture

            val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
                buildOneConfiguration("OBS_START_ACQ", "STATUS2", "gpi:status2") :: Nil
            odbActorsFactory.configure(configuration)

            val actors = odbActorsFactory.buildPrepareObservationActors(dataLabel)
            actors should have length(1)
        }
        it("should ignore unknown datalabels") {
            val (dataLabel, odbActorsFactory) = createFixture

            val actors = odbActorsFactory.buildPrepareObservationActors(new DataLabel("UNKNOWN"))
            actors should be ('empty)
        }
    }
    def createFixture = (
            dataLabel,
            new ODBActorsFactory(databaseService, programIDDatabase))

    def buildOneConfiguration(event:String,keyword:String,channel:String): GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent(event), new FitsKeyword(keyword), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("ODB"), Channel(channel), ArrayIndex("NULL"), FitsComment("A comment"))
    }

    def buildOneNonStatusConfiguration(event:String,keyword:String,channel:String): GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent(event), new FitsKeyword(keyword), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel(channel), ArrayIndex("NULL"), FitsComment("A comment"))
    }

}