package edu.gemini.aspen.gds.actors.factory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.actors.DummyActorsFactory

@RunWith(classOf[JUnitRunner])
class CompositeActorsFactoryImplSpec extends Spec with ShouldMatchers {
    def createFixture = (
            new DataLabel("GS-2011"),
            new CompositeActorsFactoryImpl("")
            )

    describe("An CompositeActorsFactory") {
        it("should return an empty list of Actors when no Factories are registered") {
            val (dataLabel, startObservationFactory) = createFixture

            val actors = startObservationFactory.buildStartAcquisitionActors(dataLabel)
            actors should be('empty)
        }
        it("should return a non empty list of actors for buildStartAcquisitionActors") {
            val (dataLabel, startObservationFactory) = createFixture

            // Register dummy factory
            startObservationFactory.bindKeywordFactory(new DummyActorsFactory())

            val actors = startObservationFactory.buildStartAcquisitionActors(dataLabel)
            actors should have length(1)
        }
        it("should return a non empty list of actors for buildPrepareObservationActors") {
            val (dataLabel, startObservationFactory) = createFixture

            // Register dummy factory
            startObservationFactory.bindKeywordFactory(new DummyActorsFactory())

            val actors = startObservationFactory.buildPrepareObservationActors(dataLabel)
            actors should have length(1)
        }
        it("should return a non empty list of actors for buildEndAcquisitionActors") {
            val (dataLabel, startObservationFactory) = createFixture

            // Register dummy factory
            startObservationFactory.bindKeywordFactory(new DummyActorsFactory())

            val actors = startObservationFactory.buildEndAcquisitionActors(dataLabel)
            actors should have length(1)
        }
        it("should return a non empty list of actors after registration and unregistration of a factory") {
            val (dataLabel, startObservationFactory) = createFixture

            // Register dummy factory
            val actorsFactory: DummyActorsFactory = new DummyActorsFactory()
            startObservationFactory.bindKeywordFactory(actorsFactory)

            // Unregister dummy factory
            startObservationFactory.unbindKeywordFactory(actorsFactory)

            val actors = startObservationFactory.buildStartAcquisitionActors(dataLabel)
            actors should be('empty)
        }
    }

}