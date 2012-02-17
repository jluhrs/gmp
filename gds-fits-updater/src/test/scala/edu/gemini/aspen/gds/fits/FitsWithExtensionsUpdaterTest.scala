package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.fits.{Header, DefaultHeaderItem, DefaultHeader, Hedit}

@RunWith(classOf[JUnitRunner])
class FitsWithExtensionsUpdaterTest extends FitsBaseTest {
  val originalFile = new File(classOf[FitsWithExtensionsUpdaterTest].getResource("FITS_WITH_EXTENSIONS.fits").toURI)
  val destinationFile = new File(originalFile.getParentFile, "N-FITS_WITH_EXTENSIONS.fits")
  val dataLabel = new DataLabel("FITS_WITH_EXTENSIONS")

  def readExtensionHeader(fitsFile: File = originalFile): Header = {
    new Hedit(fitsFile).readAllHeaders.get(1)
  }

  test("should copy a fits file with extensions before modifying it") {
    val headers = List(new DefaultHeader(1))

    updateFitsFile(headers)

    destinationFile.exists should be(true)
  }
  test("should add to an extension header a new keywords") {
    val originalExtensionHeader = readExtensionHeader(originalFile)

    verifyKeywordNotInHeader(originalExtensionHeader, "AIRMASS")

    updateFitsFile(createHeadersWithAirMass(1))

    val updatedExtensionHeader = readExtensionHeader(destinationFile)

    verifyKeywordInHeader(updatedExtensionHeader, "AIRMASS")
  }
  test("should update the extension header with several new keywords") {
    val extensionHeader = new DefaultHeader(1)
    extensionHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))
    extensionHeader.add(DefaultHeaderItem.create("AIREND", 2.0, "Mass of airmass at the end"))
    extensionHeader.add(DefaultHeaderItem.create("AIRSTART", 3.0, "Mass of airmass at the beggining"))

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

    updatedExtensionHeader.getKeywords.containsAll(originalExtensionHeader.getKeywords) should be(true)
  }
  test("should update a file in less than 0.005 secs") {
    val start = System.nanoTime
    val headers = createHeadersWithAirMass(1)
    updateFitsFile(headers)

    val spentTime = ((System.nanoTime - start) / 10e9)
    spentTime should be <= (0.005)
  }

}
