package edu.gemini.aspen.gds.keywordssets.factory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.keywordssets.DummyActorsFactory

@RunWith(classOf[JUnitRunner])
class ObservationStartActorsFactorySpec extends Spec with ShouldMatchers {
    def createFixture = (
            new DataLabel("GS-2011"),
            new ObservationStartActorsFactory()
            )

    describe("An ObservationStartActorsFactory") {
        it("should return an empty list of Actors when no Factories are registered") {
            val (dataLabel, startObservationFactory) = createFixture

            val actors = startObservationFactory.startObservationActors(dataLabel)
            actors should be('empty)
        }
        it("should return a non empty list of actors with a mocked factory") {
            val (dataLabel, startObservationFactory) = createFixture

            // Register dummy factory
            startObservationFactory.bindKeywordFactory(new DummyActorsFactory())

            val actors = startObservationFactory.startObservationActors(dataLabel)
            actors should have length(1)
        }
        it("should return a non empty list of actors after registration and unregistration of a factory") {
            val (dataLabel, startObservationFactory) = createFixture

            // Register dummy factory
            val actorsFactory: DummyActorsFactory = new DummyActorsFactory()
            startObservationFactory.bindKeywordFactory(actorsFactory)

            // Unregister dummy factory
            startObservationFactory.unbindKeywordFactory(actorsFactory)

            val actors = startObservationFactory.startObservationActors(dataLabel)
            actors should be('empty)
        }
    }

}