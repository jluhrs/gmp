package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.api.fits.{HeaderItem, Header}
import com.google.common.base.Stopwatch
import java.util.concurrent.TimeUnit

@RunWith(classOf[JUnitRunner])
class FitsWithExtensionsUpdaterTest extends FitsBaseTest {
  val originalFile = new File(classOf[FitsWithExtensionsUpdaterTest].getResource("sampleWithExt.fits").toURI)
  val destinationFile = new File(originalFile.getParentFile, "N-sampleWithExt.fits")
  val dataLabel = new DataLabel("sampleWithExt.fits")

  def readExtensionHeader(fitsFile: File = originalFile): Header = new FitsReader(fitsFile).header(1).get

  test("should copy a fits file with extensions before modifying it") {
    val headers = Nil//List(new DefaultHeader(1))

    updateFitsFile(headers)

    assertTrue(destinationFile.exists)
  }
  test("should add to an extension header a new keywords") {
    val originalExtensionHeader = readExtensionHeader(originalFile)

    verifyKeywordNotInHeader(originalExtensionHeader, "AIRMASS")

    updateFitsFile(createHeadersWithAirMass(1))

    val updatedExtensionHeader = readExtensionHeader(destinationFile)

    verifyKeywordInHeader(updatedExtensionHeader, "AIRMASS")
  }
  test("should update the extension header with several new keywords") {
    val extensionHeader = Header(1, List(HeaderItem("AIRMASS", 1.0, "Mass of airmass", None), HeaderItem("AIREND", 2.0, "Mass of airmass at the end", None), HeaderItem("AIRSTART", 3.0, "Mass of airmass at the beggining", None)))

    updateFitsFile(List(extensionHeader))

    val updatedHeader = readExtensionHeader(destinationFile)

    // Verify new headers were added
    List("AIRMASS", "AIREND", "AIRSTART") foreach {
      verifyKeywordInHeader(updatedHeader, _)
    }
  }
  test("should preseve all the original primary headers of a file") {
    val originalExtensionHeader = readExtensionHeader(originalFile)

    updateFitsFile(createHeadersWithAirMass(1))

    val updatedExtensionHeader = readExtensionHeader(destinationFile)

    originalExtensionHeader.keywords foreach {
      k => assertTrue(updatedExtensionHeader.containsKey(k.keywordName))
    }
  }
  test("should update a file in less than 300 ms") {
    val stopwatch = Stopwatch.createStarted()
    val headers = createHeadersWithAirMass(1)
    updateFitsFile(headers)

    assertTrue(stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) <= 300)
  }

}
