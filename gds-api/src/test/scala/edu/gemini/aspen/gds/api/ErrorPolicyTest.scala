package edu.gemini.aspen.gds.api

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.DataLabel
import org.mockito.Mockito._
import org.mockito.Matchers._

class ErrorPolicyTest {
  val dataLabel = new DataLabel("some key")

  @Test
  def testDefault() {
    val ep = new DefaultErrorPolicy()
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: Nil

    assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  @Test
  def testComposite() {
    val ep = new CompositeErrorPolicyImpl()
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil

    assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  @Test
  def testCompositeDefault() {
    val ep = new CompositeErrorPolicyImpl()
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil

    ep.bindPolicy(new DefaultErrorPolicy)
    assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  @Test
  def testCompositeDefaultTwice() {
    val ep = new CompositeErrorPolicyImpl()
    val collectedValues = CollectedValue[Double]("KEY1", 1.0, "comment", 0) :: ErrorCollectedValue("KEY2", CollectionError.GenericError, "comment", 0) :: Nil

    ep.bindPolicy(new DefaultErrorPolicy)
    ep.bindPolicy(new DefaultErrorPolicy)
    assertEquals(collectedValues, ep.applyPolicy(dataLabel, collectedValues))
  }

  @Test
  def testOrder() {
    val errPol1 = mock(classOf[ErrorPolicy])
    when(errPol1.priority).thenReturn(7)

    val errPol2 = mock(classOf[ErrorPolicy])
    when(errPol2.priority).thenReturn(6)

    val ep = new CompositeErrorPolicyImpl()
    ep.bindPolicy(errPol1)
    ep.bindPolicy(errPol2)
    ep.applyPolicy(dataLabel, Nil)

    val order = inOrder(errPol1, errPol2)
    order.verify(errPol2).applyPolicy(any(), any())
    order.verify(errPol1).applyPolicy(any(), any())
    order.verifyNoMoreInteractions()
  }

}