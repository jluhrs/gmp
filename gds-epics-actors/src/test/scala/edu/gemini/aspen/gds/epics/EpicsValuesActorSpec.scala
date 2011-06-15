package edu.gemini.aspen.gds.epics

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._

@RunWith(classOf[JUnitRunner])
class EpicsValuesActorSpec extends Spec with ShouldMatchers with Mockito {
    val dataLabel = new DataLabel("GS-2011")
    val epicsReader = mock[EpicsReader]

    val channelName = "ws:massAirmass"
    val referenceValue = "an epics string"
    val fitsKeyword = new FitsKeyword("AIRMASS")
    val nullValue = DefaultValue("NONE")

    def buildConfiguration(mandatory:Boolean) =
        GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", mandatory, "NONE", "EPICS", channelName, "NULL", "Mean airmass for the observation")

    describe("An EpicsValuesActor") {
        it("should reply to Collect messages") {
            val configuration = buildConfiguration(true)
            val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

            // mock return value
            epicsReader.getValue(channelName) returns referenceValue

            // Send an init message
            val result = epicsValueActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment, 0) :: Nil
                    => keyword should equal (fitsKeyword)
                       value should equal (referenceValue)
                       comment should be ("Mean airmass for the observation")
                case x:AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was one(epicsReader).getValue(channelName)
        }
        it("should provide a default value if the current one cannot be read") {
            val configuration = buildConfiguration(false)
            val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

            // mock return value cannot be read
            epicsReader.getValue(channelName) returns null

            // Send an init message
            val result = epicsValueActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment, 0) :: Nil
                    => keyword should equal (fitsKeyword)
                       value should equal (nullValue.value)
                       comment should be ("Mean airmass for the observation")
                case x:AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was one(epicsReader).getValue(channelName)
        }
        it("should return empty if mandatory and the current one cannot be read") {
            val configuration = buildConfiguration(true)
            val epicsValueActor = new EpicsValuesActor(epicsReader, configuration)

            // mock return value cannot be read
            epicsReader.getValue(channelName) returns null

            // Send an init message
            val result = epicsValueActor !! Collect
            result().asInstanceOf[List[CollectedValue[_]]].isEmpty

            // verify mock
            there was one(epicsReader).getValue(channelName)
        }
    }
}

