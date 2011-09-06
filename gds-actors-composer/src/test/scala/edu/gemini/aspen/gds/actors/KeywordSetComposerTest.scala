package edu.gemini.aspen.gds.actors

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.keywords.database.impl.KeywordsDatabaseImpl
import org.mockito.Mockito._
import org.mockito.Matchers._
import edu.gemini.aspen.gds.api.{CollectedValue, KeywordValueActor, KeywordActorsFactory}
import edu.gemini.aspen.gds.api.CollectedValue.apply
import edu.gemini.aspen.giapi.data.{FitsKeyword, ObservationEvent, DataLabel}

class KeywordSetComposerTest {
  @Test
  def startAcqMessage() {
    val (dataLabel, composer) = createFixture

    // Send an init message
    val result = composer !! AcquisitionRequest(ObservationEvent.OBS_START_ACQ, dataLabel)

    result() match {
      case AcquisitionRequestReply(obsEvent, replyDataSet) => {
        assertEquals(replyDataSet, dataLabel)
        assertEquals(obsEvent, ObservationEvent.OBS_START_ACQ)
      }
      case _ => fail("Should not reply other message")
    }
  }

  @Test
  def endAcqMessage() {
    val (dataLabel, composer) = createFixture

    // Send an init message
    val result = composer !! AcquisitionRequest(ObservationEvent.OBS_END_ACQ, dataLabel)

    result() match {
      case AcquisitionRequestReply(obsEvent, replyDataSet) => {
        assertEquals(replyDataSet, dataLabel)
        assertEquals(obsEvent, ObservationEvent.OBS_END_ACQ)
      }
      case _ => fail("Should not reply other message")
    }
  }

  @Test
  def testNoActors() {
    // Generate dataset
    val dataLabel = new DataLabel("GS-2011")

    val noActorsFactory = mock(classOf[KeywordActorsFactory])
    when(noActorsFactory.buildActors(ObservationEvent.OBS_END_READOUT, dataLabel)).thenReturn(Nil)

    val keywordsDatabase = new KeywordsDatabaseImpl()
    // Create composer
    val composer = new KeywordSetComposer(noActorsFactory, keywordsDatabase)

    // Send an init message
    val result = composer !! AcquisitionRequest(ObservationEvent.OBS_END_READOUT, dataLabel)

    result() match {
      case AcquisitionRequestReply(obsEvent, replyDataSet) => {
        assertEquals(replyDataSet, dataLabel)
        assertEquals(obsEvent, ObservationEvent.OBS_END_READOUT)
      }
      case _ => fail("Should not reply other message")
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

