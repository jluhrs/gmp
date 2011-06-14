package edu.gemini.aspen.gds.epics

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api._

@RunWith(classOf[JUnitRunner])
class EpicsActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
    val epicsReader = mock[EpicsReader]
    def createFixture = (
            new DataLabel("GS-2011"),
            new EpicsActorsFactory(epicsReader)
            )

    def buildOneConfiguration: GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
    }

    describe("An EpicsActorsFactory") {
        it("should return an empty list of Actors when not configured") {
            val (dataLabel, epicsActorsFactory) = createFixture

            val actors = epicsActorsFactory.buildStartAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should be configurable with one item") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                buildOneConfiguration
            )
            epicsActorsFactory.configure(configuration)

            val actors = epicsActorsFactory.buildStartAcquisitionActors(dataLabel)
            actors should have length(1)
        }
        it("should be configurable with two item") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            epicsActorsFactory.configure(configuration)

            val actors = epicsActorsFactory.buildStartAcquisitionActors(dataLabel)
            actors should have length(2)
        }
        it("should be configurable with one item for start and one item for end") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_END_ACQ"), new FitsKeyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            epicsActorsFactory.configure(configuration)

            val startActors = epicsActorsFactory.buildStartAcquisitionActors(dataLabel)
            startActors should have length(1)
            val endActors = epicsActorsFactory.buildEndAcquisitionActors(dataLabel)
            endActors should have length(1)
        }
        it("should only pick EPICS subsystems") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("NOEPICS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            epicsActorsFactory.configure(configuration)

            val actors = epicsActorsFactory.buildStartAcquisitionActors(dataLabel)
            actors should have length(1)
        }
    }

}