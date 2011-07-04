package edu.gemini.aspen.gds.observationstate.impl

import org.junit.Test
import edu.gemini.aspen.gds.observationstate.ObservationStateConsumer
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.Conversions._

class ObservationStatePublisherImplTest {
    @Test
    def testPublishStart() {
        val obsStatePub = new ObservationStatePublisherImpl
        val consumer: ObservationStateConsumer = mock(classOf[ObservationStateConsumer])
        obsStatePub.bindConsumer(consumer)
        obsStatePub.publishStartObservation("testlabel")
        verify(consumer, times(1)).receiveStartObservation("testlabel")
        obsStatePub.unbindConsumer(consumer)
        obsStatePub.publishStartObservation("testlabel")

        //check that it didn't get called again after unbinding
        verify(consumer, times(1)).receiveStartObservation("testlabel")
    }
}