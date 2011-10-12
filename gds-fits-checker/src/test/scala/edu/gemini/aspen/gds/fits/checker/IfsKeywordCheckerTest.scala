package edu.gemini.aspen.gds.fits.checker

import org.junit.Test
import java.io.File
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.mockito.Mockito._
import org.mockito.Matchers._
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.gds.api.Conversions._
import collection.immutable.Set.Set1
import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.aspen.gds.api.Predef._
import edu.gemini.aspen.giapi.data.{ObservationEvent, FitsKeyword}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService

class IfsKeywordCheckerTest {
  @Test
  def testWrong() {
    val obsState: ObservationStateRegistrar = mock(classOf[ObservationStateRegistrar])
    val ph: PropertyHolder = mock(classOf[PropertyHolder])
    when(ph.getProperty(anyString)).thenReturn("/tmp")
    val conf: GDSConfigurationService = mock(classOf[GDSConfigurationService])
    when(conf.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "TELSCOP", 0, "DOUBLE", true, "NONE", "IFS", "gpi:value", 0, "Mean airmass for the observation") :: Nil)
    val checker = new IfsKeywordsChecker(conf, obsState, ph)
    copy(new File(classOf[IfsKeywordCheckerTest].getResource("S20110427-01.fits").toURI), new File("/tmp/S20110427-01.fits"))
    checker.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, "S20110427-01")
    Thread.sleep(200)
    verify(obsState, times(1)).registerMissingKeyword("S20110427-01", new Set1[FitsKeyword](new FitsKeyword("TELSCOP")))
  }

  @Test
  def testOK() {
    val obsState: ObservationStateRegistrar = mock(classOf[ObservationStateRegistrar])
    val ph: PropertyHolder = mock(classOf[PropertyHolder])
    when(ph.getProperty(anyString)).thenReturn("/tmp")
    val conf: GDSConfigurationService = mock(classOf[GDSConfigurationService])
    when(conf.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "TELESCOP", 0, "DOUBLE", true, "NONE", "IFS", "gpi:value", 0, "Mean airmass for the observation") :: Nil)
    val checker = new IfsKeywordsChecker(conf, obsState, ph)
    copy(new File(classOf[IfsKeywordCheckerTest].getResource("S20110427-01.fits").toURI), new File("/tmp/S20110427-01.fits"))
    checker.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, "S20110427-01")
    Thread.sleep(200)
    verifyZeroInteractions(obsState)
  }
}