package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.gds.api.GDSObseventHandler
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.mockito.Mockito._
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar
import org.osgi.service.event.{Event, EventAdmin}

@RunWith(classOf[JUnitRunner])
class ObservationEvent2EventAdminTest extends FunSuite with MockitoSugar {

  test("basic usage") {
    val dataLabel = new DataLabel("GS-2011")
    val publisher = mock[EventAdmin]
    val translator = new ObservationEvent2EventAdmin(publisher)

    translator.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel)

    val properties: java.util.HashMap[String, (ObservationEvent, DataLabel)] = new java.util.HashMap()
    properties.put(GDSObseventHandler.ObsEventKey, (ObservationEvent.OBS_END_ACQ, dataLabel))
    verify(publisher).postEvent(new Event(GDSObseventHandler.ObsEventTopic, properties))
  }

}