package edu.gemini.aspen.gds.observationstate.impl

import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import org.joda.time.Duration
import edu.gemini.aspen.gds.api.{GDSObservationError, GDSObservationTimes, GDSEndObservation, GDSStartObservation}

@RunWith(classOf[JUnitRunner])
class GDSEventsListenerTest extends FunSuite with MockitoSugar {
  val dataLabel = new DataLabel("label")

  test("start observation") {
    val registrar = mock[ObservationStateRegistrar]
    val listener = new GDSEventsListener(registrar)

    listener.gdsEvent(GDSStartObservation(dataLabel))
    verify(registrar).startObservation(dataLabel)
  }

  test("end observation") {
    val registrar = mock[ObservationStateRegistrar]
    val listener = new GDSEventsListener(registrar)

    listener.gdsEvent(GDSEndObservation(dataLabel))
    verify(registrar).endObservation(dataLabel)
  }

  test("register times") {
    val registrar = mock[ObservationStateRegistrar]
    val listener = new GDSEventsListener(registrar)

    listener.gdsEvent(GDSObservationTimes(dataLabel, Traversable((ObservationEvent.OBS_START_ACQ, Some(new Duration(100))))))
    verify(registrar).registerTimes(same(dataLabel), any())
  }

  test("observation error") {
    val registrar = mock[ObservationStateRegistrar]
    val listener = new GDSEventsListener(registrar)

    listener.gdsEvent(GDSObservationError(dataLabel, "msg"))
    verify(registrar).registerError(dataLabel, "msg")
  }
}