package scala.edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert.{assertTrue, assertFalse, assertEquals}
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._

class OneItemKeywordValueTest extends AssertionsForJUnit {

    /**
     * Mocked actor that always returns the default value
     */
    class AlwaysDefaultValueActor(configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
        def collectValues() = defaultCollectedValue.toList
    }

    @Test
    def testDefaultCollectedValueIfMandatory() {
        val mandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", "NULL", "Mean airmass for the observation")
        assertTrue(new AlwaysDefaultValueActor(mandatoryConfig).collectValues.isEmpty)
    }

    @Test
    def testDefaultCollectedValueNonMandatory() {
        val nonMandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "gpiSvalue", "NULL", "Mean airmass for the observation")
        assertTrue(new AlwaysDefaultValueActor(nonMandatoryConfig).collectValues.nonEmpty)
        assertTrue(new AlwaysDefaultValueActor(nonMandatoryConfig).collectValues.contains(CollectedValue[String]("AIRMASS", "NONE", "Mean airmass for the observation", 0)))
    }

}