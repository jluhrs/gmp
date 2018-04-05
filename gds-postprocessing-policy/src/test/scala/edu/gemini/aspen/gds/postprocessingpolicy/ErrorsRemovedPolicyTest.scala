package edu.gemini.aspen.gds.postprocessingpolicy

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._

class ErrorsRemovedPolicyTest {
    val dataLabel = new DataLabel("some key")

    @Test
    def testNonErrors() {
        val ep = new ErrorsRemovedPolicy()
        val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil

        assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testWithOneError() {
        val ep = new ErrorsRemovedPolicy()
        val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil
        val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil

        assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testCompositeErrorRemoved() {
        val ep = new CompositePostProcessingPolicyImpl()
        val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil
        val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil
        ep.addPolicy(new ErrorsRemovedPolicy)

        assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testCompositeDefaultErrorRemoved() {
        val ep = new CompositePostProcessingPolicyImpl()
        val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil
        val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil

        ep.addPolicy(new DefaultPostProcessingPolicy)
        ep.addPolicy(new ErrorsRemovedPolicy)

        assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testCompositeErrorRemovedDefault() {
        val ep = new CompositePostProcessingPolicyImpl()
        val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil
        val filteredValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0, None) :: Nil

        ep.addPolicy(new ErrorsRemovedPolicy)
        ep.addPolicy(new DefaultPostProcessingPolicy)

        assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
    }
}