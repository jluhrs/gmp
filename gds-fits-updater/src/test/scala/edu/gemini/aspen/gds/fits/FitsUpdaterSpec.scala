package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import collection.JavaConversions._
import edu.gemini.fits.{Header, DefaultHeaderItem, DefaultHeader, Hedit}

@RunWith(classOf[JUnitRunner])
class FitsUpdaterSpec extends Spec with ShouldMatchers {
    val originalFile = new File(classOf[FitsUpdaterSpec].getResource("S20110427-01.fits").toURI)
    val dataLabel = new DataLabel("S20110427-01")

    def createHeadersWithAirMass: List[Header] = {
        val primaryHeader = new DefaultHeader()
        primaryHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))

        primaryHeader :: Nil
    }

    def updateFitsFile(headers: List[Header]) {
        val fitsUpdater = new FitsUpdater(originalFile.getParentFile, dataLabel, headers)
        fitsUpdater.updateFitsHeaders()
    }

    describe("A FitsUpdater") {
        it("should copy a fits file before modifying it") {
            val headers = List(new DefaultHeader())

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")

            destinationFile.exists should be(true)
            destinationFile.delete
        }
        it("should update the primary headers with one new keywords") {
            val primaryHeaders = new Hedit(originalFile).readPrimary

            // Verify that AIRMASS is not in the original file
            primaryHeaders.get("AIRMASS") should be(null)

            val headers = createHeadersWithAirMass

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")

            val updatedHeaders = new Hedit(destinationFile).readPrimary

            // Verify AIRMASS was added
            updatedHeaders.get("AIRMASS") should not be (null)

            destinationFile.delete
        }
        it("should update the primary headers with several new keywords") {
            val primaryHeader = new DefaultHeader()
            primaryHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))
            primaryHeader.add(DefaultHeaderItem.create("AIREND", 2.0, "Mass of airmass at the end"))
            primaryHeader.add(DefaultHeaderItem.create("AIRSTART", 3.0, "Mass of airmass at the beggining"))
            val headers = primaryHeader :: Nil

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")

            val updatedHeaders = new Hedit(destinationFile).readPrimary

            // Verify AIRMASS was added
            updatedHeaders.get("AIRMASS") should not be (null)
            updatedHeaders.get("AIREND") should not be (null)
            updatedHeaders.get("AIRSTART") should not be (null)

            destinationFile.delete
        }
        it("should preseve all the original primary headers of a file") {
            val originalKeywords = new Hedit(originalFile).readPrimary.getKeywords

            val headers = createHeadersWithAirMass

            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")

            val updatedHeaders = new Hedit(destinationFile).readPrimary

            updatedHeaders.getKeywords.containsAll(originalKeywords) should be(true)

            destinationFile.delete
        }
        it("should update a file in less than x secs") {
            val start = System.nanoTime
            val headers = createHeadersWithAirMass
            updateFitsFile(headers)

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")
            destinationFile.delete

            val spentTime = ((System.nanoTime - start) / 10e9)
            spentTime should be <= (0.01)
        }
    }
}
