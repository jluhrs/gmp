package edu.gemini.aspen.gds.keywordssets.factory

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Spec, FeatureSpec}
import edu.gemini.aspen.giapi.data.Dataset
import edu.gemini.epics.EpicsReader
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class StartObservationFactorySpec extends Spec with ShouldMatchers with MockitoSugar {
     describe("A StartObservationFactory") {
        it("should preoduce a list of Actors") {
            val epicsReader = mock[EpicsReader]
            val startObservationFactory = new StartObservationFactory(epicsReader)

            // Generate dataset
            val dataSet = new Dataset("GS-2011")
            
            val actors = startObservationFactory.startObservationActors(dataSet)
            actors should not be ('empty)
        }
    }
}