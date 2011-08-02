package edu.gemini.aspen.gds.actors

import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.gds.keywords.database.impl.KeywordsDatabaseImpl
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

@RunWith(classOf[JUnitRunner])
class KeywordSetComposerSpec extends Spec with ShouldMatchers {
  describe("A KeywordSetComposer") {
    it("should reply to StartAcquisition messages") {
      val (dataLabel, composer) = createFixture

      // Send an init message
      val result = composer !! AcquisitionRequest(ObservationEvent.OBS_START_ACQ, dataLabel)

      result() match {
        case AcquisitionRequestReply(obsEvent, replyDataSet) => {
          replyDataSet should be(dataLabel)
          obsEvent should be(ObservationEvent.OBS_START_ACQ)
        }
        case _ => fail("Should not reply other message")
      }
    }
    it("should reply to EndAcquisition messages") {
      val (dataLabel, composer) = createFixture

      // Send an init message
      val result = composer !! AcquisitionRequest(ObservationEvent.OBS_END_ACQ, dataLabel)

      result() match {
        case AcquisitionRequestReply(obsEvent, replyDataSet) => {
          replyDataSet should be(dataLabel)
          obsEvent should be(ObservationEvent.OBS_END_ACQ)
        }
        case _ => fail("Should not reply other message")
      }
    }
  }

  private def createFixture = {
    // Generate dataset
    val dataLabel = new DataLabel("GS-2011")

    val dummyActorsFactory = new DummyActorsFactory()
    val keywordsDatabase = new KeywordsDatabaseImpl()
    // Create composer
    val composer = new KeywordSetComposer(dummyActorsFactory, keywordsDatabase)
    (dataLabel, composer)
  }
}

