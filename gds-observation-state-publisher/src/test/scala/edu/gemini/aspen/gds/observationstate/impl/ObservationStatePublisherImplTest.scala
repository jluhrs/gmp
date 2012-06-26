package edu.gemini.aspen.gds.observationstate.impl

import edu.gemini.aspen.gds.observationstate.ObservationStateConsumer
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
    obsStatePub.publishEndObservation("testlabel", Nil, Nil)
    verify(consumer, times(1)).receiveEndObservation("testlabel", Nil, Nil)
    obsStatePub.unbindConsumer(consumer)
    obsStatePub.publishEndObservation("testlabel", Nil, Nil)

    //check that it didn't get called again after unbinding
    verify(consumer, times(1)).receiveEndObservation("testlabel", Nil, Nil)
  }

  test("publish observation error") {
    val obsStatePub = new ObservationStatePublisherImpl
    val consumer = mock(classOf[ObservationStateConsumer])
    obsStatePub.bindConsumer(consumer)
    obsStatePub.publishObservationError("testlabel", "file not found")
    verify(consumer, times(1)).receiveObservationError("testlabel", "file not found")
    obsStatePub.unbindConsumer(consumer)
    obsStatePub.publishObservationError("testlabel", "file not found")

    //check that it didn't get called again after unbinding
    verify(consumer, times(1)).receiveObservationError("testlabel", "file not found")
  }
}