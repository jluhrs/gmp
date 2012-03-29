package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.DataLabel
import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.mockito.Mockito._
import org.mockito.Matchers._
import java.util.logging.Logger
import org.scalatest.mock.MockitoSugar
import org.scalatest.{OneInstancePerTest, FunSuite}
import java.util.concurrent.TimeUnit

@RunWith(classOf[JUnitRunner])
class ObservationEventLoggerTest extends FunSuite with MockitoSugar with OneInstancePerTest {
  test("check time exceeded") {
    val LOG = mock[Logger]
    val logger = new ObservationEventLogger(5000L)(LOG)
    val dataLabel = new DataLabel("GS-2011")

    logger.start(dataLabel, OBS_PREP)

    assertFalse(logger.check(dataLabel, OBS_PREP, 5000L))
    logger.checkTimeWithinLimits(OBS_PREP, dataLabel)

    verify(LOG).severe(anyString)
  }

  test("check time ok") {
    val LOG = mock[Logger]
    val logger = new ObservationEventLogger(5000L)(LOG)
    val dataLabel = new DataLabel("GS-2011")

    logger.start(dataLabel, OBS_PREP)

    TimeUnit.MILLISECONDS.sleep(100)

    logger.end(dataLabel, OBS_PREP)
    println("TO CHECX " + logger.check(dataLabel, OBS_PREP, 5000L))
    assertTrue(logger.check(dataLabel, OBS_PREP, 5000L))

    logger.checkTimeWithinLimits(OBS_PREP, dataLabel)

    verifyZeroInteractions(LOG)
  }
}