package edu.gemini.aspen.gds.properties

import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}

@RunWith(classOf[JUnitRunner])
class PropertiesActorsTest extends FunSuite with BeforeAndAfter {
  before {
    System.setProperty("property.name", "property.value")
  }

  /**
   * Test for the common case of a value found in the local DB
   */
  test("base scenario") {
    val propertyActor = new PropertiesValuesActor(GDSConfiguration("GPI", "OBS_START_ACQ", "JAVAVER", 0, "STRING", true, "null", "PROPERTY", "property.name", 0, "", "my comment"))
    assertEquals(
      List(CollectedValue("JAVAVER", "property.value", "my comment", 0, None)),
      propertyActor.collectValues)
  }

  test("Wrong Type") {
    val propertyActor = new PropertiesValuesActor(GDSConfiguration("GPI", "OBS_START_ACQ", "JAVAVER", 0, "DOUBLE", true, "null", "PROPERTY", "property.name", 0, "", "my comment"))
    assertEquals(List(ErrorCollectedValue("JAVAVER", CollectionError.TypeMismatch, "my comment", 0)), propertyActor.collectValues)
  }
  /**
   * Test for a non mandatory value not found in the local DB
   */
  test("Not Mandatory, Not Found Value") {
    val propertyActor = new PropertiesValuesActor(GDSConfiguration("GPI", "OBS_START_ACQ", "JAVAVER", 0, "DOUBLE", false, "DEFAULT", "PROPERTY", "unknownproperty.name", 0, "", "my comment"))

    // should not return anything if the value cannot be read. The default will be added by an PostProcessingPolicy
    // it doesn't matter at this point if the item is mandatory or not
    assertEquals(Nil, propertyActor.collectValues)
  }

  /**
   * Test for a mandatory value not found in the local DB
   */
  test("Mandatory Not Found Value") {
    val propertyActor = new PropertiesValuesActor(GDSConfiguration("GPI", "OBS_START_ACQ", "JAVAVER", 0, "DOUBLE", true, "DEFAULT", "PROPERTY", "unknownproperty.name", 0, "", "my comment"))

    // should not return anything if the value cannot be read. The default will be added by an PostProcessingPolicy
    // it doesn't matter at this point if the item is mandatory or not
    assertEquals(Nil, propertyActor.collectValues)
  }

}