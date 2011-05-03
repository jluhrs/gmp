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
    describe("A FitsUpdater") {
        it("should copy a fits file before modifying it") {
            val file = new File(classOf[FitsUpdaterSpec].getResource("S20110427-01.fits").toURI)
            val dataLabel = new DataLabel("S20110427-01")

            val primaryHeader = new DefaultHeader()
            val headers = List(primaryHeader)

            val fitsUpdater = new FitsUpdater(file.getParentFile, dataLabel, headers)
            fitsUpdater.updateFitsHeaders

            val destinationFile = new File(file.getParentFile, "N-S20110427-01.fits")

            destinationFile.exists should be (true)
        }
        it("should update the primary headers of a file copy") {
            val file = new File(classOf[FitsUpdaterSpec].getResource("S20110427-01.fits").toURI)
            val dataLabel = new DataLabel("S20110427-01")

            val primaryHeaders = new Hedit(file).readPrimary

            primaryHeaders.get("AIRMASS") should be (null)

            val airmassItem = DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass")
            val primaryyHeader = new DefaultHeader(List(airmassItem))
            primaryyHeader.add(airmassItem)
            val headers = List(primaryyHeader)
            val fitsUpdater = new FitsUpdater(file.getParentFile, dataLabel, headers)
            fitsUpdater.updateFitsHeaders

            val destinationFile = new File(file.getParentFile, "N-S20110427-01.fits")

            val updatedHeaders = new Hedit(destinationFile).readPrimary

            updatedHeaders.get("AIRMASS") should not be (null)
        }
    }
}
