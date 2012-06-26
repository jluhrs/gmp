package scala.edu.gemini.aspen.gds.api

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import scala.collection._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class OneItemKeywordValueTest extends FunSuite {

  class TestValueActor(configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    def collectValues() = immutable.List[CollectedValue[_]]()

    def testValueToCollectedValue(value: Any) = valueToCollectedValue(value)
  }

  test("Double Value To CollectedValue") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    assertEquals(CollectedValue("AIRMASS", 1.0, "comment", 0, None), testActor.testValueToCollectedValue(1.0))
    assertEquals(CollectedValue("AIRMASS", 1.0, "comment", 0, None), testActor.testValueToCollectedValue(1))
    assertEquals(ErrorCollectedValue("AIRMASS", CollectionError.TypeMismatch, "comment", 0), testActor.testValueToCollectedValue("1"))
  }

  test("Int Value To CollectedValue"){
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "INT", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    assertEquals(ErrorCollectedValue("AIRMASS", CollectionError.TypeMismatch, "comment", 0), testActor.testValueToCollectedValue(1.1))
    assertEquals(CollectedValue("AIRMASS", 1, "comment", 0, None), testActor.testValueToCollectedValue(1))
    assertEquals(ErrorCollectedValue("AIRMASS", CollectionError.TypeMismatch, "comment", 0), testActor.testValueToCollectedValue("1"))
  }

  test("String Value To CollectedValue") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "STRING", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    assertEquals(CollectedValue("AIRMASS", "1.0", "comment", 0, None), testActor.testValueToCollectedValue(1.0))
    assertEquals(CollectedValue("AIRMASS", "1", "comment", 0, None), testActor.testValueToCollectedValue(1))
    assertEquals(CollectedValue("AIRMASS", "1", "comment", 0, None), testActor.testValueToCollectedValue("1"))
  }

  test("Boolean Value To CollectedValue from Integer") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    // Integer to boolean
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(1))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(2))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(-1))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue(0))
  }

  test("Boolean Value To CollectedValue from Long") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    // Long to boolean
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(1L))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(2L))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(-1L))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue(0L))
  }

  test("Boolean Value To CollectedValue from Float/Double") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    // Float to boolean
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(1f))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(2f))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(-1f))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue(0f))
    // Double to boolean
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(1.0))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(2.0))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(-1.0))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue(0.0))
  }

  test("Boolean Value To CollectedValue from String") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    // Float to boolean
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue("true"))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue("True"))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue("1"))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue("T"))
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue("Anything"))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue("false"))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue("False"))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue("F"))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue("f"))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue("0"))
  }

   test("Boolean Value To CollectedValue from Boolean") {
    val config = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "NONE", "EPICS", "gpi:value", 0, "", "comment")
    val testActor = new TestValueActor(config)
    // Float to boolean
    assertEquals(CollectedValue("AIRMASS", true, "comment", 0, None), testActor.testValueToCollectedValue(true))
    assertEquals(CollectedValue("AIRMASS", false, "comment", 0, None), testActor.testValueToCollectedValue(false))
  }
}