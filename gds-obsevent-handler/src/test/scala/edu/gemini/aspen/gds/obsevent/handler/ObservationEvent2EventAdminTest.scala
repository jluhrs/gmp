package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.apache.felix.ipojo.handlers.event.publisher.Publisher

@RunWith(classOf[JUnitRunner])
class ObservationEvent2EventAdminTest extends FunSuite with MockitoSugar {

  test("basic usage") {
    val dataLabel = new DataLabel("GS-2011")
    val publisher = mock[Publisher]
    val translator = new ObservationEvent2EventAdmin(publisher)

    translator.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel)

    verify(publisher).sendData(ObservationEvent.OBS_END_ACQ, dataLabel)
  }

}