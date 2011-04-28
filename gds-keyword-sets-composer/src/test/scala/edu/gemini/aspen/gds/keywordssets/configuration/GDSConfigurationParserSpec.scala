package edu.gemini.aspen.gds.keywordssets.configuration

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers


@RunWith(classOf[JUnitRunner])
class GDSConfigurationParserSpec extends Spec with ShouldMatchers {
    describe("A GDSConfigurationParser") {
        it("should parse files with one compliant line") {
            val parser = GDSConfigurationParser
            parser.main(null)
        }
    }
}