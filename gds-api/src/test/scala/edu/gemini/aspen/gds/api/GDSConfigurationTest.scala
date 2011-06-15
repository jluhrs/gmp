package scala.edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert.{assertTrue, assertFalse}
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._

class GDSConfigurationTest extends AssertionsForJUnit {

    @Test
    def testIsMandatory() {
        val mandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", "NULL", "Mean airmass for the observation")
        assertTrue(mandatoryConfig.isMandatory)
        val nonMandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "gpiSvalue", "NULL", "Mean airmass for the observation")
        assertFalse(nonMandatoryConfig.isMandatory)
    }

}