package edu.gemini.aspen.gds.status

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.StatusDatabaseService

@RunWith(classOf[JUnitRunner])
class InstrumentStatusActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
    val statusDB = mock[StatusDatabaseService]

    def createFixture = (
            new DataLabel("GS-2011"),
            new InstrumentStatusActorsFactory(statusDB))

    def buildOneConfiguration(event:String,keyword:String,channel:String): GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent(event), new FitsKeyword(keyword), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel(channel), ArrayIndex("NULL"), FitsComment("A comment"))
    }

    def buildOneNonStatusConfiguration(event:String,keyword:String,channel:String): GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent(event), new FitsKeyword(keyword), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("NOSTATUS"), Channel(channel), ArrayIndex("NULL"), FitsComment("A comment"))
    }

    describe("An InstrumentStatusActorsFactory") {
        it("should return an empty list of Actors when not configured") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should be configurable with one item") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") :: Nil
            instrumentStatusActorsFactory.configure(configuration)

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(1)
        }
        it("should be configurable with two item") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture

            val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
                buildOneConfiguration("OBS_START_ACQ", "STATUS2", "gpi:status2") :: Nil
            instrumentStatusActorsFactory.configure(configuration)

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(2)
        }
        it("should be configurable with one item for start and one item for end") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
                buildOneConfiguration("OBS_END_ACQ", "STATUS2", "gpi:status2") :: Nil
            instrumentStatusActorsFactory.configure(configuration)

            val startActors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            startActors should have length(1)
            val endActors = instrumentStatusActorsFactory.endAcquisitionActors(dataLabel)
            endActors should have length(1)
        }
        it("should only pick Instrument Status subsystems") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = buildOneConfiguration("OBS_START_ACQ", "STATUS1", "gpi:status1") ::
                buildOneNonStatusConfiguration("OBS_END_ACQ", "STATUS2", "gpi:status2") :: Nil
            instrumentStatusActorsFactory.configure(configuration)

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(1)
        }
    }

}