package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._
import org.junit.Assert._
import org.junit.{Ignore, Test}

/**
 * Specify how the GDSConfiguration parser should behave
 */
class GDSConfigurationParserTest {
    val text = """GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 "Mean airmass for the observation""""

    val good = Array("GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"",
        "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0     \"Mean airmass for the observation\"")
    val comments = Array("#", "# ", "#comment")
    val wrong = Array("GPI OBS_END_AQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //wrong obs event
        "GPI OBS_END_ACQ AIRMASS22 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //keyword too long
        "GPI OBS_END_ACQ AIRMASS A DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //header index must be positive integer
        "GPI OBS_END_ACQ AIRMASS 0 FLOAT F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //wrong data type
        "GPI OBS_END_ACQ AIRMASS 0 DOUBLE y NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //mandatory must be [FfTt]
        "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE MAGIC ws:massAirmass 0 \"Mean airmass for the observation\"", //unknown subsystem
        "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass -1 \"Mean airmass for the observation\"", //array index must be positive integer
        "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 Mean airmass for the observation") //comment must be surrounded in "
    val blank = Array("", " ", "\t")


    @Test
    def testWrong {
        for (line <- wrong) {
            val result = new GDSConfigurationParser().parseText(line)
            assertFalse(result.successful)
        }
    }

    @Ignore
    @Test
    def testBlank {
        for (line <- blank) {
            val result = new GDSConfigurationParser().parseText(line)
            assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
            assertFalse(result.isEmpty)
            assertEquals(List(), result.get)
        }
    }

    @Test
    def testComments {
        for (line <- comments) {
            val result = new GDSConfigurationParser().parseText(line)
            assertTrue(result.successful)
            assertFalse(result.isEmpty)

            assertEquals(Comment(line), result.get(0))
        }
    }

    @Test
    def testParseConfigurationLine {
        for (line <- good) {
            val result = new GDSConfigurationParser().parseText(text)
            assertTrue(result.successful)

            assertFalse(result.isEmpty)
            assertEquals(GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_END_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem(KeywordSource.EPICS), Channel("ws:massAirmass"), ArrayIndex(0), FitsComment("Mean airmass for the observation")), result.get(0))
        }

    }

    @Test
    def testParseCommentLines {
        val text = "#comment"
        val result = new GDSConfigurationParser().parseText(text)

        assertTrue(result.successful)

        assertFalse(result.isEmpty)
        assertEquals(Comment("#comment"), result.get(0))
    }
}