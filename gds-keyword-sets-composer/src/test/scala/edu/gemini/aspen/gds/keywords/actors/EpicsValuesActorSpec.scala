package edu.gemini.aspen.gds.keywords.actors

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.gds.keywordssets.{CollectedValue, Collect}
import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.{FitsKeyword, Dataset}
import org.specs2.mock.Mockito

@RunWith(classOf[JUnitRunner])
class EpicsValuesActorSpec extends Spec with ShouldMatchers with Mockito {
    describe("An EpicsValuesActor") {
        it("should reply to Collect messages") {
            // Generate dataset
            val dataSet = new Dataset("GS-2011")
            val epicsReader = mock[EpicsReader]

            val channelName = "channelName"
            val referenceValue = "an epics string"
            // mock return value
            epicsReader.getValue(channelName) returns referenceValue
            val fitsKeyword = new FitsKeyword("KEYWORD1")

            val epicsValueActor = new EpicsValuesActor(epicsReader, fitsKeyword, channelName)
            epicsValueActor.start
            
            // Send an init message
            val result = epicsValueActor !! Collect

            result() match {
                case CollectedValue(keyword, value, comment)
                    => keyword should equal (fitsKeyword)
                       value should equal (referenceValue)
                       comment should be ('empty)
                case _ => fail("Should not reply other message")
            }

            // verify mock
            there was one(epicsReader).getValue(channelName)
        }
    }
}

