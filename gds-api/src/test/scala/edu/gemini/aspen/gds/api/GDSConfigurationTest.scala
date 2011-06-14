package scala.edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert.{assertTrue, assertFalse}
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._

class GDSConfigurationTest extends AssertionsForJUnit {

    @Test
    def testIsMandatory() {
        val mandatoryConfig = GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(true), NullValue("NONE"), Subsystem("EPICS"), Channel("a"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
        assertTrue(mandatoryConfig.isMandatory)
        val nonMandatoryConfig = GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_START_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("EPICS"), Channel("a"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation"))
        assertFalse(nonMandatoryConfig.isMandatory)
    }

}