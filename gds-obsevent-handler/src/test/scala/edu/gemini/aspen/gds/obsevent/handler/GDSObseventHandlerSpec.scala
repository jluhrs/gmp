package edu.gemini.aspen.gds.obsevent.handler

import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import org.junit.runner.RunWith
import edu.gemini.aspen.gds.keywordssets.factory.StartAcquisitionActorsFactory
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import actors.Actor

@RunWith(classOf[JUnitRunner])
class GDSObseventHandlerSpec extends Spec with ShouldMatchers with Mockito {
    describe("A GDSObseventHandler") {
        it("should react to OBS_START_ACQ events") {
            val actorsFactory = mock[StartAcquisitionActorsFactory]
            val observationHandler = new GDSObseventHandler(actorsFactory)
            val dataLabel = new DataLabel("GS-2011")

            actorsFactory.startObservationActors(dataLabel) returns List[Actor]()

            observationHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel)

            // verify mock
            there was one(actorsFactory).startObservationActors(dataLabel)
        }
        it("should react to OBS_END_ACQ events")(pending)
        it("should not react to other events messages")(pending)
    }
}