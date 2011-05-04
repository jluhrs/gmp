package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import io.Source
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.fits.{DefaultHeaderItem, DefaultHeader, Hedit}
import collection.JavaConversions._

@RunWith(classOf[JUnitRunner])
class FitsUpdaterSpec extends Spec with ShouldMatchers {
    val originalFile = new File(classOf[FitsUpdaterSpec].getResource("S20110427-01.fits").toURI)
    val dataLabel = new DataLabel("S20110427-01")

    describe("A FitsUpdater") {
        it("should copy a fits file before modifying it") {
            val headers = List(new DefaultHeader())

            val fitsUpdater = new FitsUpdater(originalFile.getParentFile, dataLabel, headers)
            fitsUpdater.updateFitsHeaders

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")

            destinationFile.exists should be (true)
            destinationFile.delete
        }
        it("should update the primary headers of a file copy") {
            // Verify that AIRMASS is not in the original file
            val primaryHeaders = new Hedit(originalFile).readPrimary

            primaryHeaders.get("AIRMASS") should be (null)

            val airmassHeaderItem = DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass")
            val primaryHeader = new DefaultHeader()
            primaryHeader.add(airmassHeaderItem)

            val headers = List(primaryHeader)
            val fitsUpdater = new FitsUpdater(originalFile.getParentFile, dataLabel, headers)
            fitsUpdater.updateFitsHeaders

            val destinationFile = new File(originalFile.getParentFile, "N-S20110427-01.fits")

            val updatedHeaders = new Hedit(destinationFile).readPrimary

            updatedHeaders.get("AIRMASS") should not be (null)

            destinationFile.delete
        }
    }
}
