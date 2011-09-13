package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import collection.JavaConversions._
import edu.gemini.fits.{Header, DefaultHeaderItem, DefaultHeader, Hedit}

@RunWith(classOf[JUnitRunner])
class FitsUpdaterSpec extends FitsBaseSpec {
  val originalFile = new File(classOf[FitsUpdaterSpec].getResource("S20110427-01.fits").toURI)
  val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")
  val dataLabel = new DataLabel("S20110427-01")

  def readPrimaryHeader(fitsFile: File = originalFile): Header = {
    new Hedit(fitsFile).readAllHeaders.get(0)
  }

  describe("A FitsUpdater") {
    it("should copy a fits file before modifying it") {
      val headers = List(new DefaultHeader())

      updateFitsFile(headers)

      destinationFile.exists should be(true)
    }
    it("should also work when used as an actor") {
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
    it("should update the primary headers with one new keywords") {
      val primaryHeader = readPrimaryHeader(originalFile)

      // Verify that AIRMASS is not in the original file
      verifyKeywordNotInHeader(primaryHeader, "AIRMASS")

      val headers = createHeadersWithAirMass(0)

      updateFitsFile(headers)

      val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

      // Verify AIRMASS was added
      verifyKeywordInHeader(updatedPrimaryHeader, "AIRMASS")
    }
    it("should update the primary headers with several new keywords") {
      val primaryHeader = new DefaultHeader()
      primaryHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))
      primaryHeader.add(DefaultHeaderItem.create("AIREND", 2.0, "Mass of airmass at the end"))
      primaryHeader.add(DefaultHeaderItem.create("AIRSTART", 3.0, "Mass of airmass at the beggining"))
      val headers = primaryHeader :: Nil

      updateFitsFile(headers)

      val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

      // Verify AIRMASS was added
      List("AIRMASS", "AIREND", "AIRSTART") foreach {
        verifyKeywordInHeader(updatedPrimaryHeader, _)
      }
    }
    it("should preseve all the original primary headers of a file") {
      val originalPrimaryHeader = readPrimaryHeader(originalFile)

      val headers = createHeadersWithAirMass(0)

      updateFitsFile(headers)

      val updatedPrimaryHeader = readPrimaryHeader(destinationFile)

      updatedPrimaryHeader.getKeywords.containsAll(originalPrimaryHeader.getKeywords) should be(true)
    }
    it("should update a file in less than 0.005 secs") {
      val start = System.nanoTime
      val headers = createHeadersWithAirMass(0)
      updateFitsFile(headers)

      val spentTime = ((System.nanoTime - start) / 10e9)
      spentTime should be <= (0.005)
    }
  }

}
