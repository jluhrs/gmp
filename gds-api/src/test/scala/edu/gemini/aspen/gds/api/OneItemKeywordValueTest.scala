package scala.edu.gemini.aspen.gds.api

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.junit.Assert.assertTrue
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api._

class OneItemKeywordValueTest extends AssertionsForJUnit {

    /**
     * Mocked actor that always returns the default value
     */
    class AlwaysDefaultValueActor(configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
        def collectValues() = defaultCollectedValue.toList
    }

    @Test
    def testDefaultCollectedValueIfMandatory() {
        val mandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", true, "NONE", "EPICS", "gpi:value", 0, "Mean airmass for the observation")
        assertTrue(new AlwaysDefaultValueActor(mandatoryConfig).collectValues.nonEmpty)
        assertTrue(new AlwaysDefaultValueActor(mandatoryConfig).collectValues.contains(ErrorCollectedValue("AIRMASS", CollectionError.MandatoryRequired, "Mean airmass for the observation", 0)))
    }
    @Test
    def testDefaultCollectedValueNonMandatory() {
        val nonMandatoryConfig = GDSConfiguration("GPI", "OBS_START_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", "EPICS", "gpiSvalue", 0, "Mean airmass for the observation")
        assertTrue(new AlwaysDefaultValueActor(nonMandatoryConfig).collectValues.nonEmpty)
        assertTrue(new AlwaysDefaultValueActor(nonMandatoryConfig).collectValues.contains(CollectedValue("AIRMASS", "NONE", "Mean airmass for the observation", 0)))
    }

}