package edu.gemini.aspen.gds.fits.checker

import java.io.File
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.mockito.Mockito._
import org.mockito.Matchers._
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.gds.api.Conversions._
import collection.immutable.Set
import edu.gemini.aspen.gmp.services.PropertyHolder
import edu.gemini.aspen.gds.api.Predef._
import edu.gemini.aspen.giapi.data.ObservationEvent
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FunSuite}
import java.util.concurrent.TimeUnit
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class IfsKeywordCheckerTest extends FunSuite with BeforeAndAfter with MockitoSugar {
  val destFile = new File("/tmp/sample.fits")
  val obsState = mock[ObservationStateRegistrar]
  val ph = mock[PropertyHolder]
  when(ph.getProperty(anyString)).thenReturn("/tmp")

  test("missing keywords") {
    val conf = mock[GDSConfigurationService]
    when(conf.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "TELSCOP", 0, "DOUBLE", true, "NONE", "IFS", "gpi:value", 0, "Mean airmass for the observation") :: Nil)

    val checker = new IfsKeywordsChecker(conf, obsState, ph)
    copy(new File(classOf[IfsKeywordCheckerTest].getResource("sample.fits").toURI), destFile)

    checker.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, "sample")
    TimeUnit.MILLISECONDS.sleep(100)
    verify(obsState, times(1)).registerMissingKeyword("sample", Set[FitsKeyword](new FitsKeyword("TELSCOP")))
  }

  test("check ok"){
    val conf = mock[GDSConfigurationService]
    when(conf.getConfiguration).thenReturn(GDSConfiguration("GPI", "OBS_START_ACQ", "TELESCOP", 0, "DOUBLE", true, "NONE", "IFS", "gpi:value", 0, "Mean airmass for the observation") :: Nil)

    val checker = new IfsKeywordsChecker(conf, obsState, ph)
    copy(new File(classOf[IfsKeywordCheckerTest].getResource("sample.fits").toURI), new File("/tmp/sample.fits"))

    checker.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, "sample")
    TimeUnit.MILLISECONDS.sleep(100)
    verifyZeroInteractions(obsState)
  }

  after {
    destFile.delete()
  }
}