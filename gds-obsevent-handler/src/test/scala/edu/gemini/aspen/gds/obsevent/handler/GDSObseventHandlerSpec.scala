package edu.gemini.aspen.gds.obsevent.handler

import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import org.junit.runner.RunWith
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabaseImpl
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.actors.AcquisitionRequestReply
import edu.gemini.aspen.gds.api.{CompositeErrorPolicyImpl, CompositeErrorPolicy, KeywordValueActor}

@RunWith(classOf[JUnitRunner])
class GDSObseventHandlerSpec extends Spec with ShouldMatchers with Mockito {
    val actorsFactory = mock[CompositeActorsFactory]
    val keywordsDatabase = new KeywordsDatabaseImpl()

    private val observationHandler = new GDSObseventHandler(actorsFactory, keywordsDatabase, new CompositeErrorPolicyImpl())
    describe("A GDSObseventHandler") {
        it("should react to OBS_PREP events") {

            val dataLabel = new DataLabel("GS-2011")

            actorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel) returns List[KeywordValueActor]()

            observationHandler.onObservationEvent(ObservationEvent.OBS_PREP, dataLabel)

            Thread.sleep(100)
            observationHandler.replyHandler ! AcquisitionRequestReply(ObservationEvent.OBS_PREP, dataLabel)
            Thread.sleep(100)

            // verify mock
            there was one(actorsFactory).buildActors(ObservationEvent.OBS_PREP, dataLabel)
        }
        it("should react to OBS_START_ACQ events") {

            val dataLabel = new DataLabel("GS-2011")

            actorsFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel) returns List[KeywordValueActor]()

            Thread.sleep(100)
            observationHandler.replyHandler ! AcquisitionRequestReply(ObservationEvent.OBS_START_ACQ, dataLabel)
            Thread.sleep(100)

            observationHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel)

            // verify mock
            there was one(actorsFactory).buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
        }
        it("should react to OBS_END_ACQ events") {
            val dataLabel = new DataLabel("GS-2011")

            actorsFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel) returns List[KeywordValueActor]()

            observationHandler.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel)

            Thread.sleep(100)
            observationHandler.replyHandler ! AcquisitionRequestReply(ObservationEvent.OBS_END_ACQ, dataLabel)
            Thread.sleep(100)

            // verify mock
            there was one(actorsFactory).buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
        }
        it("should not react to other events messages")(pending)
    }
}