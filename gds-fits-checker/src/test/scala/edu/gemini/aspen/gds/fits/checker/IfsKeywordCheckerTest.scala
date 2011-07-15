package edu.gemini.aspen.gds.fits.checker

import org.junit.Test
import java.io.File
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.mockito.Mockito._
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.FitsKeyword
import collection.immutable.Set.Set1

class IfsKeywordCheckerTest {
    @Test
    def testWrong() {
        val obsState: ObservationStateRegistrar = mock(classOf[ObservationStateRegistrar])
        val checker = new IfsKeywordsChecker(null, obsState)
        checker.checkMissing("S20110427-01", new File(classOf[IfsKeywordCheckerTest].getResource("S20110427-01.fits").toURI), GDSConfiguration("GPI", "OBS_START_ACQ", "TELSCOP", 0, "DOUBLE", true, "NONE", "IFS", "gpi:value", 0, "Mean airmass for the observation") :: Nil)
        verify(obsState, times(1)).registerMissingKeyword("S20110427-01", new Set1[FitsKeyword](new FitsKeyword("TELSCOP")))
    }

    @Test
    def testOK() {
        val obsState: ObservationStateRegistrar = mock(classOf[ObservationStateRegistrar])
        val checker = new IfsKeywordsChecker(null, obsState)
        checker.checkMissing("S20110427-01", new File(classOf[IfsKeywordCheckerTest].getResource("S20110427-01.fits").toURI), GDSConfiguration("GPI", "OBS_START_ACQ", "TELESCOP", 0, "DOUBLE", true, "NONE", "IFS", "gpi:value", 0, "Mean airmass for the observation") :: Nil)
        verifyZeroInteractions(obsState)
    }
}