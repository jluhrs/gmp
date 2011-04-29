package edu.gemini.aspen.gds.keywordssets.configuration

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class GDSConfigurationParserSpec extends Spec with ShouldMatchers {
    describe("A GDSConfigurationParser") {
        it("should parse configuration line") {
            val text = """      GPI          OBS_END_ACQ      AIRMASS       DOUBLE          F            NONE        EPICS       ws:massAirmass	              NULL     "Mean airmass for the observation""""
            val parser = new GDSConfigurationParser()
            val result = parser.parseText(text)

            result.successful should be (true)

            result.get should have length(1)
            result.get(0) should equal (Configuration(Instrument("GPI"), ObservationEvent("OBS_END_ACQ"), Keyword("AIRMASS"), DataType("DOUBLE"), Mandatory(false), NullValue("NONE"), Subsystem("EPICS"), Channel("ws:massAirmass"), ArrayIndex("NULL"), FitsComment("Mean airmass for the observation")))
        }
        it("should parse comment lines") {
            val text = "#comment"
            val parser = new GDSConfigurationParser()
            val result = parser.parseText(text)

            result.successful should be (true)

            result.get should have length(1)
            result.get(0) should equal (Comment("#comment"))
        }
//        it("should parse blank lines") {
//            val text = ""
//            val parser = new GDSConfigurationParser()
//            val result = parser.parseText(text)
//
//            result.successful should be (true)
//
//            result.get should have length(1)
//            result.get(0) should equal (Space(0))
//        }
    }
}