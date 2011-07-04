package edu.gemini.aspen.gds.observationstate.impl

import org.junit.Assert._
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.DataLabel
import org.junit.Test
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher

class ObservationStateImplTest {

    @Test
    def testObsInProgress() {
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock(classOf[GDSConfigurationService]), mock(classOf[ObservationStatePublisher]))
        assertTrue(obsState.getObservationsInProgress.isEmpty)

        obsState.startObservation("label1")
        assertEquals(new DataLabel("label1"), obsState.getObservationsInProgress.head)
        assertEquals(obsState.getObservationsInProgress.size, 1)

        obsState.startObservation("label2")
        obsState.endObservation("label2")
        assertEquals(new DataLabel("label1"), obsState.getObservationsInProgress.head)
        assertEquals(obsState.getObservationsInProgress.size, 1)
    }
}