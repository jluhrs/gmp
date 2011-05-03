package edu.gemini.aspen.gds.fits

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.matchers.ShouldMatchers
import io.Source
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel

@RunWith(classOf[JUnitRunner])
class FitsUpdaterSpec extends Spec with ShouldMatchers {
    describe("A FitsUpdater") {
        it("should copy a fits file before modifying it") {
            val file = new File(classOf[FitsUpdaterSpec].getResource("S20110427-01.fits").toURI)
            val dataLabel = new DataLabel("S20110427-01")

            val fitsUpdater = new FitsUpdater(file.getParentFile, dataLabel, null)
            fitsUpdater.updateFitsHeaders

            val destinationFile = new File(file.getParentFile, "N-S20110427-01.fits")
            println(destinationFile.getAbsolutePath)

            destinationFile.exists should be (true)
        }
        it("should update the primary headers of a file copy") (pending)
    }
}
