package scala.edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert.{assertTrue, assertFalse}
import edu.gemini.aspen.gds.api.Mandatory
import edu.gemini.aspen.gds.api.GDSConfiguration._

class GDSConfigurationTest extends AssertionsForJUnit {

    @Test
    def testConversion() {
        assertTrue(Mandatory(true))
        assertFalse(Mandatory(false))
    }


}