package edu.gemini.aspen.gds.observationstate.impl

import org.junit.Assert._
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectionError
import collection.immutable.Set.Set2

class ObservationStateImplTest {

    @Test
    def testObsInProgress() {
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
        assertTrue(obsState.getObservationsInProgress.isEmpty)

        obsState.startObservation("label1")
        assertEquals(new DataLabel("label1"), obsState.getObservationsInProgress.head)
        assertEquals(obsState.getObservationsInProgress.size, 1)

        obsState.startObservation("label2")
        obsState.endObservation("label2")
        assertEquals(new DataLabel("label1"), obsState.getObservationsInProgress.head)
        assertEquals(obsState.getObservationsInProgress.size, 1)
    }

    @Test
    def testLastDataLabel() {
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
        obsState.startObservation("label1")
        obsState.startObservation("label2")
        obsState.endObservation("label2")
        assertEquals(Some(new DataLabel("label2")), obsState.getLastDataLabel)

        obsState.endObservation("label1")
        assertEquals(Some(new DataLabel("label1")), obsState.getLastDataLabel)
    }

    @Test
    def testMissing() {
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
        obsState.startObservation("label1")
        obsState.registerMissingKeyword("label1", new FitsKeyword("a") :: Nil)
        obsState.registerMissingKeyword("label1", new FitsKeyword("b") :: Nil)
        assertEquals((new FitsKeyword("b") :: new FitsKeyword("a") :: Nil).toSet, obsState.getMissingKeywords("label1").toSet)
    }

    @Test
    def testError() {
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[ObservationStatePublisher]))
        obsState.startObservation("label1")
        obsState.registerCollectionError("label1", List((new FitsKeyword("a"), CollectionError.GenericError)))
        obsState.registerCollectionError("label1", List((new FitsKeyword("b"), CollectionError.GenericError)))
        assertEquals(new Set2((new FitsKeyword("b"), CollectionError.GenericError), (new FitsKeyword("a"), CollectionError.GenericError)), obsState.getKeywordsInError("label1").toSet)
    }
}