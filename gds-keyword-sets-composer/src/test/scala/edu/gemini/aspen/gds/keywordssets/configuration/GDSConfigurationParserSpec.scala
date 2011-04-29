package edu.gemini.aspen.gds.keywordssets.configuration

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class GDSConfigurationParserSpec extends Spec with ShouldMatchers {
    describe("A GDSConfigurationParser") {
        it("should parse comment lines") {
            val text = "#comment"
            val parser = new GDSConfigurationParser()
            val result = parser.parseText(text)

            result.successful should be (true)

            result.get should have length(1)
            result.get(0) should equal (Comment("#comment"))
        }
    }
}