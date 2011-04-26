package edu.gemini.aspen.gds.keywordssets.factory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.Spec
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.keywordssets.DummyActorsFactory
import org.specs2.mock.Mockito
import edu.gemini.epics.EpicsReader

@RunWith(classOf[JUnitRunner])
class EpicsActorsFactorySpec extends Spec with ShouldMatchers with Mockito {
    val epicsReader = mock[EpicsReader]
    def createFixture = (
            new DataLabel("GS-2011"),
            new EpicsActorsFactory(epicsReader)
            )

    describe("An EpicsActorsFactory") {
        it("should not return an empty list of Actors") {
            val (dataLabel, epicsActorsFactory) = createFixture

            val actors = epicsActorsFactory.startAcquisitionActors(dataLabel)
            actors should not be('empty)
        }
    }

}