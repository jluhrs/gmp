package edu.gemini.aspen.gds.observationstate.impl

import edu.gemini.aspen.gds.observationstate.{ObservationError, Successful, ObservationInfo, ObservationStateConsumer}
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class ObservationStatePublisherImplTest extends FunSuite {
  test("publish start") {
    val obsStatePub = new ObservationStatePublisherImpl
    val consumer = mock(classOf[ObservationStateConsumer])
    obsStatePub.bindConsumer(consumer)
    obsStatePub.publishStartObservation("testlabel")
    verify(consumer, times(1)).receiveStartObservation("testlabel")
    obsStatePub.unbindConsumer(consumer)
    obsStatePub.publishStartObservation("testlabel")

    //check that it didn't get called again after unbinding
    verify(consumer, times(1)).receiveStartObservation("testlabel")
  }

  test("publish end observation") {
    val obsStatePub = new ObservationStatePublisherImpl
    val consumer = mock(classOf[ObservationStateConsumer])
    obsStatePub.bindConsumer(consumer)
    val info = new ObservationInfo("testlabel", Successful)
    obsStatePub.publishEndObservation(info)
    verify(consumer, times(1)).receiveEndObservation(info)
    obsStatePub.unbindConsumer(consumer)
    obsStatePub.publishEndObservation(info)

    //check that it didn't get called again after unbinding
    verify(consumer, times(1)).receiveEndObservation(info)
  }

  test("publish observation error") {
    val obsStatePub = new ObservationStatePublisherImpl
    val consumer = mock(classOf[ObservationStateConsumer])
    obsStatePub.bindConsumer(consumer)
    val info = new ObservationInfo("testlabel", ObservationError, errorMsg = Some("some error"))
    obsStatePub.publishObservationError(info)
    verify(consumer, times(1)).receiveObservationError(info)
    obsStatePub.unbindConsumer(consumer)
    obsStatePub.publishObservationError(info)

    //check that it didn't get called again after unbinding
    verify(consumer, times(1)).receiveObservationError(info)
  }
}