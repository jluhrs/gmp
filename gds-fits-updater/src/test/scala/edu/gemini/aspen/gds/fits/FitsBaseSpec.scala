package edu.gemini.aspen.gds.fits

import org.scalatest.matchers.ShouldMatchers
import java.io.File
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.fits.{Header, DefaultHeaderItem, DefaultHeader}
import org.scalatest.{BeforeAndAfterEach, Spec}

abstract class FitsBaseSpec extends Spec with ShouldMatchers with BeforeAndAfterEach {
    val originalFile: File
    val destinationFile: File
    val dataLabel: DataLabel

    def createHeadersWithAirMass(headerIndex:Int) : List[Header] = {
        val primaryHeader = new DefaultHeader(headerIndex)
        primaryHeader.add(DefaultHeaderItem.create("AIRMASS", 1.0, "Mass of airmass"))

        primaryHeader :: Nil
    }

    def updateFitsFile(headers: List[Header]) {
        val fitsUpdater = new FitsUpdater(originalFile.getParentFile, dataLabel, headers)
        fitsUpdater.updateFitsHeaders()
    }

    def verifyKeywordInHeader(header: Header, keyword: String): Unit = {
        header.get(keyword) should not be(null)
    }

    def verifyKeywordNotInHeader(header: Header, keyword: String): Unit = {
        header.get(keyword) should be(null)
    }

    override def afterEach() = if (destinationFile.exists) {
        destinationFile.delete
    }
}
