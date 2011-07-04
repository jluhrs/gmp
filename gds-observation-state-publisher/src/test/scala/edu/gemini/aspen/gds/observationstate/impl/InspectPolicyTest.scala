package edu.gemini.aspen.gds.observationstate.impl

import org.junit.Assert._
import org.mockito.Mockito._
import org.junit.Test
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher
import edu.gemini.aspen.gds.api.ErrorPolicy

class InspectPolicyTest {
    @Test
    def test() {
        val policy: ErrorPolicy = new InspectPolicy(mock(classOf[ObservationStatePublisher]))
        assertEquals(0, policy.priority)
    }

}