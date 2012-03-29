package edu.gemini.aspen.gds.obsevent.handler

import org.junit.Test
import edu.gemini.aspen.giapi.data.ObservationEvent._
import org.junit.Assert._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class ObsEventBookKeepingTest extends FunSuite {

  test("previous") {
    val book = new ObsEventBookKeeping
    val dataLabel = new DataLabel("GS-2011")

    book.addObs(OBS_PREP, dataLabel)
    assertTrue(book.obsArrived(OBS_PREP, dataLabel))
    assertFalse(book.obsArrived(OBS_START_ACQ, dataLabel))
    assertTrue(book.previousArrived(OBS_START_ACQ, dataLabel))
    assertFalse(book.previousArrived(OBS_END_ACQ, dataLabel))
  }

  test("reply") {
    val book = new ObsEventBookKeeping
    val dataLabel = new DataLabel("GS-2011")

    book.addReply(OBS_PREP, dataLabel)
    assertTrue(book.replyArrived(OBS_PREP, dataLabel))
    assertFalse(book.replyArrived(OBS_START_ACQ, dataLabel))
    assertFalse(book.allRepliesArrived(dataLabel))

  }

  test("all events") {
    val book = new ObsEventBookKeeping
    val dataLabel = new DataLabel("GS-2011")

    for (evt <- ObservationEvent.values()) {
      book.addObs(evt, dataLabel)
      book.addReply(evt, dataLabel)
    }
    assertTrue(book.allRepliesArrived(dataLabel))

    book.clean(dataLabel)

    assertFalse(book.obsArrived(OBS_PREP, dataLabel))
  }

  test("all non external events") {
    val book = new ObsEventBookKeeping
    val dataLabel = new DataLabel("GS-2011")

    for (evt <- ObservationEvent.values() filter {_.getObservationEventName.startsWith("OBS")}) {
      book.addObs(evt, dataLabel)
      book.addReply(evt, dataLabel)
    }
    assertTrue(book.allRepliesArrived(dataLabel))

    book.clean(dataLabel)

    assertFalse(book.obsArrived(OBS_PREP, dataLabel))
  }
}