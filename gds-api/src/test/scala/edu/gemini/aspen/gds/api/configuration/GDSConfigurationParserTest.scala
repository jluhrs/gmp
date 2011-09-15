package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._
import org.junit.Assert._
import org.junit.Test
import java.io.File
import edu.gemini.aspen.gds.api.Predef.copy

/**
 * Specify how the GDSConfiguration parser should behave
 */
class GDSConfigurationParserTest {
  val text = """GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 "Mean airmass for the observation""""

  val good = Array("GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"",
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0     \"Mean airmass for the observation\"",
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0     \"Mean airmass #for the observation\"", //# in middle of comment
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0     \"\"", //empty comment
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NO#NE        EPICS       ws:massAirmass	              0     \"Mean airmass for the observation\"") //#in middle
  val comments = Array("#", "# ", "#comment", " # comment with leading space", "  # comment with 2 leading spaces", "\t#comment with leading tab")
  val wrong = Array("GPI OBS_END_AQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //wrong obs event
    "GPI OBS_END_ACQ AIRMASS22 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //keyword too long
    "GPI OBS_END_ACQ AIRMASS A DOUBLE F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //header index must be positive integer
    "GPI OBS_END_ACQ AIRMASS 0 FLOAT F NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //wrong data type
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE y NONE EPICS ws:massAirmass 0 \"Mean airmass for the observation\"", //mandatory must be [FfTt]
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE MAGIC ws:massAirmass 0 \"Mean airmass for the observation\"", //unknown subsystem
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass -1 \"Mean airmass for the observation\"", //array index must be positive integer
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 Mean airmass for the observation", //comment must be surrounded in "
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 ", //no comment
    "comment with missing #")
  val blank = Array("", " ", "  ", "\t")


  @Test
  def testWrong() {
    for (line <- wrong) {
      val result = new GDSConfigurationParser().parseText(line)
      assertFalse("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
    }
  }

  @Test
  def testBlank() {
    for (line <- blank) {
      val result = new GDSConfigurationParser().parseText(line)
      assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
      assertFalse(result.isEmpty)
      assertEquals(List(None), result.get)
    }
  }

  @Test
  def testComments() {
    for (line <- comments) {
      val result = new GDSConfigurationParser().parseText(line)
      assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
      assertFalse(result.isEmpty)

      assertEquals(Some(Comment(line.trim())), result.get(0))
    }
  }

  @Test
  def testParseConfigurationLine() {
    for (line <- good) {
      val result = new GDSConfigurationParser().parseText(text)
      assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)

      assertFalse(result.isEmpty)
      assertEquals(Some(GDSConfiguration(Instrument("GPI"), GDSEvent("OBS_END_ACQ"), new FitsKeyword("AIRMASS"), HeaderIndex(0), DataType("DOUBLE"), Mandatory(false), DefaultValue("NONE"), Subsystem(KeywordSource.EPICS), Channel("ws:massAirmass"), ArrayIndex(0), FitsComment("Mean airmass for the observation"))), result.get(0))
    }

  }

  @Test
  def testParseCommentLines() {
    val text = "#comment"
    val result = new GDSConfigurationParser().parseText(text)

    assertTrue(result.successful)

    assertFalse(result.isEmpty)
    assertEquals(Some(Comment("#comment")), result.get(0))
  }

  @Test
  def parseFile() {
    copy(new File(this.getClass.getResource("gds-keywords.conf").toURI), new File("/tmp/gds-keywords.conf.test"))
    val result = new GDSConfigurationParser().parseFileRawResult("/tmp/gds-keywords.conf.test")
    assertTrue(result.successful)

    assertFalse(result.isEmpty)
    assertEquals(16, result.get.size)
  }
}