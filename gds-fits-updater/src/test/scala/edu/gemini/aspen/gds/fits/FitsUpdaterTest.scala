package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.fits.{HeaderItem, Header}
import edu.gemini.aspen.gds.api.Conversions._
import com.google.common.base.Stopwatch
import java.util.concurrent.TimeUnit

@RunWith(classOf[JUnitRunner])
class FitsUpdaterTest extends FitsBaseTest {
  val originalFile = new File(classOf[FitsUpdaterTest].getResource("sample1.fits").toURI)
  val destinationFile = new File(originalFile.getParentFile, "N-sample1.fits")
  val dataLabel = new DataLabel("sample1.fits")

  def readPrimaryHeader(fitsFile: File = originalFile): Header = new FitsReader(fitsFile).header().get

  test("copy a fits file before modifying it") {
    val headers = Header(0, Nil)

    updateFitsFile(headers :: Nil)

    assertTrue(destinationFile.exists)
  }

  test("should also work when used as an actor") {
    val primaryHeader = readPrimaryHeader(originalFile)

    // Verify that AIRMASS is not in the original file
    verifyKeywordNotInHeader(primaryHeader, "AIRMASS")

    val headers = createHeadersWithAirMass(0)

    val fitsUpdater = new FitsUpdater(originalFile.getParentFile, destinationFile.getParentFile, dataLabel, headers)
    fitsUpdater ! Update({
      label => destinationFile.getName
    })
    Thread.sleep(500)
    val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

    // Verify AIRMASS was added
    verifyKeywordInHeader(updatedPrimaryHeader, "AIRMASS")
  }

  test("should update the primary headers with one new keywords") {
    val primaryHeader = readPrimaryHeader(originalFile)

    // Verify that AIRMASS is not in the original file
    verifyKeywordNotInHeader(primaryHeader, "AIRMASS")

    val headers = createHeadersWithAirMass(0)

    updateFitsFile(headers)

    val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

    // Verify AIRMASS was added
    verifyKeywordInHeader(updatedPrimaryHeader, "AIRMASS")
  }

  test("should update the primary headers with several new keywords") {
    val keys = List(HeaderItem("AIRMASS", 1.0, "Mass of airmass", None),HeaderItem("AIREND", 2.0, "Mass of airmass at the end", None),HeaderItem("AIRSTART", 3.0, "Mass of airmass at the beggining", None))
    val primaryHeader = Header(0, keys)
    val headers = primaryHeader :: Nil

    updateFitsFile(headers)

    val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

    // Verify AIRMASS was added
    List("AIRMASS", "AIREND", "AIRSTART") foreach {
      verifyKeywordInHeader(updatedPrimaryHeader, _)
    }
  }

  test("should preseve all the original primary headers of a file") {
    val originalPrimaryHeader = readPrimaryHeader(originalFile)

    val headers = createHeadersWithAirMass(0)

    updateFitsFile(headers)

    val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

    originalPrimaryHeader.keywords foreach {
      k => assertTrue(updatedPrimaryHeader.containsKey(k.keywordName))
    }
  }

  test("should update a file in less than 300 msecs") {
    val stopwatch = new Stopwatch().start
    val headers = createHeadersWithAirMass(0)
    updateFitsFile(headers)

    assertTrue(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) <= 300)
  }

  test("should skip updating an extension if not present, bug GIAPI-879") {
    val originalPrimaryHeader = readPrimaryHeader(originalFile)

    // The update goes to the header 1 that doesn't exist
    val headers = createHeadersWithAirMass(1)

    updateFitsFile(headers)

    val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

    originalPrimaryHeader.keywords foreach {
      k => assertTrue(updatedPrimaryHeader.containsKey(k.keywordName))
    }
    assertFalse(updatedPrimaryHeader.containsKey("AIRMASS"))
  }

  test("should support files with or without file extension") {
    assertEquals("DATALABEL", FitsUpdater.toFitsFileName("DATALABEL"))

    assertEquals("DATALABEL.fits", FitsUpdater.toFitsFileName("DATALABEL.fits"))
  }

}