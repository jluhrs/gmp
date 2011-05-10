package edu.gemini.aspen.gds.status

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api._

@RunWith(classOf[JUnitRunner])
class InstrumentStatusActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
    def createFixture = (
            new DataLabel("GS-2011"),
            new InstrumentStatusActorsFactory
            )

    def buildOneConfiguration: GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
    }

    describe("An InstrumentStatusActorsFactory") {
        it("should return an empty list of Actors when not configured") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should be configurable with one item") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = List(
                buildOneConfiguration
            )
            instrumentStatusActorsFactory.configure(configuration)

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(1)
        }
        it("should be configurable with two item") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            instrumentStatusActorsFactory.configure(configuration)

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(2)
        }
        it("should be configurable with one item for start and one item for end") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_END_ACQ"), new FitsKeyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            instrumentStatusActorsFactory.configure(configuration)

            val startActors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            startActors should have length(1)
            val endActors = instrumentStatusActorsFactory.endAcquisitionActors(dataLabel)
            endActors should have length(1)
        }
        it("should only pick Instrument Status subsystems") {
            val (dataLabel, instrumentStatusActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("STATUS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("NOSTATUS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            instrumentStatusActorsFactory.configure(configuration)

            val actors = instrumentStatusActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(1)
        }
    }

}