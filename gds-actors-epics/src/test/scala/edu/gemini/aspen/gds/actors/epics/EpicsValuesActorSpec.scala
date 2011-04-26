package edu.gemini.aspen.gds.actors.epics

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.actors.{CollectedValue, Collect}

@RunWith(classOf[JUnitRunner])
class EpicsValuesActorSpec extends Spec with ShouldMatchers with Mockito {
    describe("An EpicsValuesActor") {
        it("should reply to Collect messages") {
            // Generate dataset
            val dataLabel = new DataLabel("GS-2011")
            val epicsReader = mock[EpicsReader]

            val channelName = "channelName"
            val referenceValue = "an epics string"
            // mock return value
            epicsReader.getValue(channelName) returns referenceValue
            val fitsKeyword = new FitsKeyword("KEYWORD1")

            val epicsValueActor = new EpicsValuesActor(epicsReader, fitsKeyword, channelName)
            
            // Send an init message
            val result = epicsValueActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment) :: Nil
                    => keyword should equal (fitsKeyword)
                       value should equal (referenceValue)
                       comment should be ('empty)
                case x:AnyRef => fail("Should not reply other message ")
            }

            // verify mock
            there was one(epicsReader).getValue(channelName)
        }
    }
}

