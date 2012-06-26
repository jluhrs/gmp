package scala.edu.gemini.aspen.gds.api

import org.junit.Assert._
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class GDSConfigurationTest extends FunSuite {

  test("mandatory attribute") {
    val mandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation")
    assertTrue(mandatoryConfig.isMandatory)
    val nonMandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "gpiSvalue", 0, "", "Mean airmass for the observation")
    assertFalse(nonMandatoryConfig.isMandatory)
  }

  test("default value") {
    val defaultWithDecimals = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "0.0", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation")
    assertEquals("0.0", defaultWithDecimals.nullValue.value)
    val defaultNoDecimals = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "0", "EPICS", "gpiSvalue", 0, "", "Mean airmass for the observation")
    assertEquals("0", defaultNoDecimals.nullValue.value)
  }

  test("boolean type") {
    val booleanKeyword = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "T", "EPICS", "gpi:value", 0, "", "Mean airmass for the observation")
    assertEquals("BOOLEAN", booleanKeyword.dataType.name)
    assertEquals("T", booleanKeyword.nullValue.value)
  }

  test("property source") {
    val booleanKeyword = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "BOOLEAN", true, "T", "PROPERTY", "gpi:value", 0, "", "Mean airmass for the observation")
    assertEquals("PROPERTY", booleanKeyword.subsystem.name.toString)
  }

}