package edu.gemini.aspen.gds.obsevent.handler

import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import org.specs2.mock.Mockito
import org.junit.runner.RunWith
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import actors.Actor
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabaseImpl
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory

@RunWith(classOf[JUnitRunner])
class GDSObseventHandlerSpec extends Spec with ShouldMatchers with Mockito {
  val actorsFactory = mock[CompositeActorsFactory]
  val keywordsDatabase = new KeywordsDatabaseImpl()

  private val observationHandler = new GDSObseventHandler(actorsFactory, keywordsDatabase)
  describe("A GDSObseventHandler") {
    it("should react to OBS_START_ACQ events") {

      val dataLabel = new DataLabel("GS-2011")

      actorsFactory.buildStartAcquisitionActors(dataLabel) returns List[Actor]()

      observationHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel)

      // verify mock
      there was one(actorsFactory).buildStartAcquisitionActors(dataLabel)
    }
    it("should react to OBS_END_ACQ events") {
      val dataLabel = new DataLabel("GS-2011")

      actorsFactory.buildEndAcquisitionActors(dataLabel) returns List[Actor]()

      observationHandler.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel)

      // verify mock
      there was one(actorsFactory).buildEndAcquisitionActors(dataLabel)
    }
    it("should not react to other events messages")(pending)
  }
}