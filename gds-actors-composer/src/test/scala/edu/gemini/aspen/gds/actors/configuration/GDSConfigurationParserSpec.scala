package edu.gemini.aspen.gds.actors.configuration

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._

/**
 * Specify how the GDSConfiguration parser should behave
 */
@RunWith(classOf[JUnitRunner])
class GDSConfigurationParserSpec extends Spec with ShouldMatchers {
    val text = """GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass NULL "Mean airmass for the observation""""

    describe("A GDSConfigurationParser") {
        it("should parse configuration line") {
            val text = """      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              NULL     "Mean airmass for the observation""""
            verifyParsedLine(text)
        }
        it("should parse configuration line with minimal spaces") {
            verifyParsedLine(text)
        }
        it("should read the default value") {
            val result = new GDSConfigurationParser().parseText(text)

            result.get(0).asInstanceOf[GDSConfiguration].nullValue should equal (DefaultValue("NONE"))
        }
        it("should parse comment lines") {
            val text = "#comment"
            val result = new GDSConfigurationParser().parseText(text)

            result.successful should be (true)

            result.get should have length(1)
            result.get(0) should equal (Comment("#comment"))
        }
    }

    def verifyParsedLine(text: String): Any = {
        val result = new GDSConfigurationParser().parseText(text)
        result.successful should be(true)

        result.get should have length (1)
        result.get(0) should equal(GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_END_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")))
    }

}