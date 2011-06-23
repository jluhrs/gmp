package edu.gemini.aspen.gds.obsevent.handler

import org.junit.Test
import edu.gemini.aspen.giapi.data.ObservationEvent._
import org.junit.Assert._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

class ObsEventBookKeepingTest {

    @Test
    def test() {
        val book = new ObsEventBookKeeping
        val dataLabel = new DataLabel("GS-2011")

        book.addObs(OBS_PREP, dataLabel)
        assertTrue(book.obsArrived(OBS_PREP, dataLabel))
        assertFalse(book.obsArrived(OBS_START_ACQ, dataLabel))
        assertTrue(book.previousArrived(OBS_START_ACQ, dataLabel))
        assertFalse(book.previousArrived(OBS_END_ACQ, dataLabel))

        book.addReply(OBS_PREP, dataLabel)
        assertTrue(book.replyArrived(OBS_PREP, dataLabel))
        assertFalse(book.replyArrived(OBS_START_ACQ, dataLabel))

        assertFalse(book.allRepliesArrived(dataLabel))

        for (evt <- ObservationEvent.values()) {
            book.addObs(evt, dataLabel)
            book.addReply(evt, dataLabel)
        }
        assertTrue(book.allRepliesArrived(dataLabel))

        book.clean(dataLabel)

        assertFalse(book.obsArrived(OBS_PREP, dataLabel))
    }
}