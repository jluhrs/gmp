package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Assert._
import java.io.File
import edu.gemini.aspen.gds.api.Predef.copy
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

/**
 * Specify how the GDSConfiguration parser should behave
 */
@RunWith(classOf[JUnitRunner])
class GDSConfigurationParserTest extends FunSuite {
  val text = """GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 "" "Mean airmass for the observation""""

  val good = Array("GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"%20.13G\" \"Mean airmass for the observation\"",
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0    \"Bla: %f\" \"Mean airmass for the observation\"",
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0    \"\" \"Mean airmass #for the observation\"", //# in middle of comment
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NONE        EPICS       ws:massAirmass	              0    \"\" \"\"", //empty comment
    "      GPI          OBS_END_ACQ      AIRMASS       0    DOUBLE          F            NO#NE        EPICS       ws:massAirmass	              0   \"\"  \"Mean airmass for the observation\"") //#in middle
  val comments = Array("#", "# ", "#comment", " # comment with leading space", "  # comment with 2 leading spaces",  "\t#comment with leading tab")
  val wrong = Array("GPI OBS_END_AQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"\" \"Mean airmass for the observation\"", //wrong obs event
    "GPI OBS_END_ACQ AIRMASS22 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"\" \"Mean airmass for the observation\"", //keyword too long
    "GPI OBS_END_ACQ AIRMASS A DOUBLE F NONE EPICS ws:massAirmass 0 \"\" \"Mean airmass for the observation\"", //header index must be positive integer
    "GPI OBS_END_ACQ AIRMASS 0 FLOAT F NONE EPICS ws:massAirmass 0 \"\" \"Mean airmass for the observation\"", //wrong data type
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE y NONE EPICS ws:massAirmass 0 \"\" \"Mean airmass for the observation\"", //mandatory must be [FfTt]
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE MAGIC ws:massAirmass 0 \"\" \"Mean airmass for the observation\"", //unknown subsystem
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass -1 \"\" \"Mean airmass for the observation\"", //array index must be positive integer
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"bla\" \"Mean airmass for the observation\"", //format is incorrect
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"\" Mean airmass for the observation", //comment must be surrounded in "
    "GPI OBS_END_ACQ AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 \"\" ", //no comment
    "comment with missing #")
  val blank = Array("", " ", "  ", "\t")

  test("catch bad values") {
    for (line <- wrong) {
      val result = new GDSConfigurationParser().parseText(line)
      assertFalse("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
    }
  }

  test("parse bad values") {
    val parser = new GDSConfigurationParser()
    val result = parser.parseText(wrong(0))
    result match {
      case parser.Failure(msg, k) =>
        assertEquals("string matching regex `OBS_PREP|OBS_START_ACQ|OBS_END_ACQ|OBS_START_READOUT|OBS_END_READOUT|OBS_START_DSET_WRITE|OBS_END_DSET_WRITE|EXT_START_OBS|EXT_END_OBS' expected but `O' found", msg)
        assertEquals(1, k.pos.line)
        assertEquals(5, k.pos.column)
        assertFalse(k.atEnd)
        assertEquals(4, k.offset)
      case _ => fail()
    }
  }

  test("parse blank lines") {
    for (line <- blank) {
      val result = new GDSConfigurationParser().parseText(line)
      assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
      assertFalse(result.isEmpty)
      assertEquals(List(None), result.get)
    }
  }

  test("parse comments") {
    for (line <- comments) {
      val result = new GDSConfigurationParser().parseText(line)
      assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)
      assertFalse(result.isEmpty)

      assertEquals(Some(Comment(line.trim())), result.get(0))
    }
  }

  test("parse configuration line") {
    for (line <- good) {
      val result = new GDSConfigurationParser().parseText(text)
      assertTrue("Error parsing line with following contents: ->|" + line + "|<-", result.successful)

      assertFalse(result.isEmpty)
      assertEquals(Some(GDSConfiguration("GPI", "OBS_END_ACQ", "AIRMASS", 0, "DOUBLE", false, "NONE", KeywordSource.EPICS.toString, "ws:massAirmass", 0, "", "Mean airmass for the observation")), result.get(0))
    }
  }

  test("parse comment lines") {
    val text = "#comment"
    val result = new GDSConfigurationParser().parseText(text)

    assertTrue(result.successful)

    assertFalse(result.isEmpty)
    assertEquals(Some(Comment("#comment")), result.get(0))
  }

  test("parse bad files") {
    copy(new File(this.getClass.getResource("gds-keywords.conf").toURI), new File("/tmp/gds-keywords.conf.test"))
    val result = new GDSConfigurationParser().parseFileRawResult("/tmp/gds-keywords.conf.test")
    assertTrue(result.successful)

    assertFalse(result.isEmpty)
    assertEquals(16, result.get.size)
  }

  test("parse default with non supported values. Bug GIAPI-871") {
    val result = new GDSConfigurationParser().parseText("""GPI OBS_END_ACQ AIRMASS 0 DOUBLE F 0.0 EPICS ws:massAirmass 0 "" "Mean airmass for the observation"""")
    assertTrue(result.successful)

    assertEquals(DefaultValue("0.0"), result.get(0).get.asInstanceOf[GDSConfiguration].nullValue)

    // Test with negative
    val result2 = new GDSConfigurationParser().parseText("""GPI OBS_END_ACQ AIRMASS 0 DOUBLE F -0.1 EPICS ws:massAirmass 0 "" "Mean airmass for the observation"""")
    assertTrue(result2.successful)

    assertEquals(DefaultValue("-0.1"), result2.get(0).get.asInstanceOf[GDSConfiguration].nullValue)

    // Test with a dash
    val result3 = new GDSConfigurationParser().parseText("""GPI OBS_END_ACQ AIRMASS 0 DOUBLE F HAWAII-R2 EPICS ws:massAirmass 0 "" "Mean airmass for the observation"""")
    assertTrue(result3.successful)

    assertEquals(DefaultValue("HAWAII-R2"), result3.get(0).get.asInstanceOf[GDSConfiguration].nullValue)
  }

  test("parse channel with a dot. Bug GIAPI-878") {
    val result = new GDSConfigurationParser().parseText("""GPI OBS_END_ACQ AIRMASS 0 DOUBLE F 0.0 STATUS gpi:cc.value1 0 "" "Mean airmass for the observation"""")
    assertTrue(result.successful)

    assertEquals(Channel("gpi:cc.value1"), result.get(0).get.asInstanceOf[GDSConfiguration].channel)
  }

  test("parse channel with square parentheses. Bug GIAPI-963") {
    val result = new GDSConfigurationParser().parseText("""GPI OBS_END_ACQ AIRMASS 0 DOUBLE F 0.0 STATUS gpi:cc.value[1] 0 "" "Mean airmass for the observation"""")
    assertTrue(result.successful)

    assertEquals(Channel("gpi:cc.value[1]"), result.get(0).get.asInstanceOf[GDSConfiguration].channel)
  }

  test("supports external start observation event") {
    val text = """GPI EXT_START_OBS AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 "" "Mean airmass for the observation""""
    val result = new GDSConfigurationParser().parseText(text)
    assertEquals(Some(GDSConfiguration("GPI", "EXT_START_OBS", "AIRMASS", 0, "DOUBLE", false, "NONE", KeywordSource.EPICS.toString, "ws:massAirmass", 0, "", "Mean airmass for the observation")), result.get(0))
  }

  test("supports external ond observation event") {
    val text = """GPI EXT_END_OBS AIRMASS 0 DOUBLE F NONE EPICS ws:massAirmass 0 "" "Mean airmass for the observation""""
    val result = new GDSConfigurationParser().parseText(text)
    assertEquals(Some(GDSConfiguration("GPI", "EXT_END_OBS", "AIRMASS", 0, "DOUBLE", false, "NONE", KeywordSource.EPICS.toString, "ws:massAirmass", 0, "", "Mean airmass for the observation")), result.get(0))
  }

  test("supports constant values in quotes") {
    val text = """GPI EXT_END_OBS AIRMASS 0 DOUBLE F "A CONSTANT VALUE" CONSTANT none 0 "" "Constant value""""
    val result = new GDSConfigurationParser().parseText(text)
    assertEquals(Some(GDSConfiguration("GPI", "EXT_END_OBS", "AIRMASS", 0, "DOUBLE", false, "A CONSTANT VALUE", KeywordSource.CONSTANT.toString, "none", 0, "", "Constant value")), result.get(0))
  }

  test("supports constant values with one char") {
    val text = """GPI EXT_END_OBS AIRMASS 0 BOOLEAN F T CONSTANT none 0 "" "Constant value""""
    val result = new GDSConfigurationParser().parseText(text)
    assertEquals(Some(GDSConfiguration("GPI", "EXT_END_OBS", "AIRMASS", 0, "BOOLEAN", false, "T", KeywordSource.CONSTANT.toString, "none", 0, "", "Constant value")), result.get(0))
  }
}