package edu.gemini.aspen.gds.actors.epics

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.DataLabel
import org.specs2.mock.Mockito
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.keywordssets.configuration._

@RunWith(classOf[JUnitRunner])
class EpicsActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
    val epicsReader = mock[EpicsReader]
    def createFixture = (
            new DataLabel("GS-2011"),
            new EpicsActorsFactory(epicsReader)
            )

    def buildOneConfiguration: GDSConfiguration = {
        GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), Keyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
    }

    describe("An EpicsActorsFactory") {
        it("should return an empty list of Actors when not configured") {
            val (dataLabel, epicsActorsFactory) = createFixture

            val actors = epicsActorsFactory.startAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should be configurable with one item") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                buildOneConfiguration
            )
            epicsActorsFactory.configure(configuration)

            val actors = epicsActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(1)
        }
        it("should be configurable with two item") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), Keyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), Keyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            epicsActorsFactory.configure(configuration)

            val actors = epicsActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(2)
        }
        it("should only pick EPICS subsystems") {
            val (dataLabel, epicsActorsFactory) = createFixture
            val configuration = List(
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), Keyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")),
                GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), Keyword("AIRMAS2"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("NOEPICS"), Channel("ws:massAirmas2"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
            )
            epicsActorsFactory.configure(configuration)

            val actors = epicsActorsFactory.startAcquisitionActors(dataLabel)
            actors should have length(1)
        }
    }

}