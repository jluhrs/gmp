package scala.edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert.assertTrue
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import org.mockito.Mockito._
import org.junit.Assert._

class OneItemKeywordValueTest extends AssertionsForJUnit {

  class TestValueActor(configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    def collectValues(): List[CollectedValue[_]] = Nil

    def testValueToCollectedValue(value: Any) = valueToCollectedValue(value)
  }

  @Test
  def testDoubleValueToCollectedValue() {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "comment")
    val testActor = new TestValueActor(config)
    assertEquals(CollectedValue("AIRMASS", 1.0, "comment", 0), testActor.testValueToCollectedValue(1.0))
    assertEquals(CollectedValue("AIRMASS", 1.0, "comment", 0), testActor.testValueToCollectedValue(1))
    assertEquals(ErrorCollectedValue("AIRMASS", CollectionError.TypeMismatch, "comment", 0), testActor.testValueToCollectedValue("1"))
  }

  @Test
  def testIntValueToCollectedValue() {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "INT", true, "NONE", "EPICS", "gpi:value", 0, "comment")
    val testActor = new TestValueActor(config)
    assertEquals(ErrorCollectedValue("AIRMASS", CollectionError.TypeMismatch, "comment", 0), testActor.testValueToCollectedValue(1.1))
    assertEquals(CollectedValue("AIRMASS", 1, "comment", 0), testActor.testValueToCollectedValue(1))
    assertEquals(ErrorCollectedValue("AIRMASS", CollectionError.TypeMismatch, "comment", 0), testActor.testValueToCollectedValue("1"))
  }

  @Test
  def testStringValueToCollectedValue() {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "STRING", true, "NONE", "EPICS", "gpi:value", 0, "comment")
    val testActor = new TestValueActor(config)
    assertEquals(CollectedValue("AIRMASS", "1.0", "comment", 0), testActor.testValueToCollectedValue(1.0))
    assertEquals(CollectedValue("AIRMASS", "1", "comment", 0), testActor.testValueToCollectedValue(1))
    assertEquals(CollectedValue("AIRMASS", "1", "comment", 0), testActor.testValueToCollectedValue("1"))
  }
}