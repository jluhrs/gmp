package edu.gemini.aspen.gds.errorpolicy

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import scala.Option
import edu.gemini.aspen.gds.api.{CollectionError, ErrorCollectedValue, CollectedValue}

class ErrorsRemovedPolicyTest {
    val dataLabel = new DataLabel("some name")

    @Test
    def testNonErrors() {
        val ep = new ErrorsRemovedPolicy()
        val collectedValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: Nil)

        assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testWithOneError() {
        val ep = new ErrorsRemovedPolicy()
        val collectedValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil)
        val filteredValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: Nil)

        assertEquals(filteredValues, ep.applyPolicy(dataLabel, collectedValues))
    }
}