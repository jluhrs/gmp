package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.fits.{Header, DefaultHeaderItem, DefaultHeader, Hedit}

@RunWith(classOf[JUnitRunner])
class FitsWithExtensionsUpdaterSpec extends Spec with ShouldMatchers {
    val originalFile = new File(classOf[FitsWithExtensionsUpdaterSpec].getResource("FITS_WITH_EXTENSIONS.fits").toURI)
    val dataLabel = new DataLabel("FITS_WITH_EXTENSIONS")

    def createHeadersWithAirMass: List[Header] = {
        val primaryHeader = new DefaultHeader(1)
        primaryHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))

        primaryHeader :: Nil
    }

    def updateFitsFile(headers: List[Header]) {
        val fitsUpdater = new FitsUpdater(originalFile.getParentFile, dataLabel, headers)
        fitsUpdater.updateFitsHeaders
    }

    describe("A FitsUpdater") {
        it("should copy a fits file with extensions before modifying it") {
            val headers = List(new DefaultHeader(1))

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-FITS_WITH_EXTENSIONS.fits")

            destinationFile.exists should be(true)
            destinationFile.delete
        }
        it("should add to an extension header a new keywords") {
            val extensionHeader = new Hedit(originalFile).readAllHeaders.get(1)

            // Verify that AIRMASS is not in the original file
            extensionHeader.get("AIRMASS") should be(null)

            val headers = createHeadersWithAirMass

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-FITS_WITH_EXTENSIONS.fits")

            val updatedHeaders = new Hedit(destinationFile).readAllHeaders.get(1)

            // Verify AIRMASS was added
            updatedHeaders.get("AIRMASS") should not be (null)

            destinationFile.delete
        }
        it("should update the extension header with several new keywords") {
            val extensionHeader = new DefaultHeader(1)
            extensionHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))
            extensionHeader.add(DefaultHeaderItem.create("AIREND", 2.0, "Mass of airmass at the end"))
            extensionHeader.add(DefaultHeaderItem.create("AIRSTART", 3.0, "Mass of airmass at the beggining"))
            val headers = extensionHeader :: Nil

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-FITS_WITH_EXTENSIONS.fits")

            val updatedHeader = new Hedit(destinationFile).readAllHeaders.get(1)

            // Verify new headers were added
            updatedHeader.get("AIRMASS") should not be (null)
            updatedHeader.get("AIREND") should not be (null)
            updatedHeader.get("AIRSTART") should not be (null)

            destinationFile.delete
        }
        it("should preseve all the original primary headers of a file") {
            val originalKeywords = new Hedit(originalFile).readAllHeaders.get(1)

            val headers = createHeadersWithAirMass

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-FITS_WITH_EXTENSIONS.fits")

            val updatedHeaders = new Hedit(destinationFile).readAllHeaders.get(1)

            updatedHeaders.getKeywords.containsAll(originalKeywords.getKeywords) should be(true)

            destinationFile.delete
        }
        it("should update a file in less than 0.1 secs") {
            val start = System.nanoTime
            val headers = createHeadersWithAirMass
            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-FITS_WITH_EXTENSIONS.fits")
            destinationFile.delete

            val spentTime = ((System.nanoTime - start) / 10e9)
            spentTime should be <= (0.1)
        }
    }
}
