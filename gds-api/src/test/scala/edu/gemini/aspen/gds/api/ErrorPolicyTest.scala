package edu.gemini.aspen.gds.api

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.DataLabel

class ErrorPolicyTest {
    val dataLabel = new DataLabel("some name")

    @Test
    def testDefault() {
        val ep = new DefaultErrorPolicy()
        val collectedValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: Nil)

        assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testComposite() {
        val ep = new CompositeErrorPolicyImpl()
        val collectedValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil)

        assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testCompositeDefault() {
        val ep = new CompositeErrorPolicyImpl()
        val collectedValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil)

        ep.bindPolicy(new DefaultErrorPolicy)
        assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
    }

    @Test
    def testCompositeDefaultTwice() {
        val ep = new CompositeErrorPolicyImpl()
        val collectedValues = Option(CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil)

        ep.bindPolicy(new DefaultErrorPolicy)
        ep.bindPolicy(new DefaultErrorPolicy)
        assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
    }


}