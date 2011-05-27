package edu.gemini.aspen.gds.fits

import edu.gemini.aspen.giapi.data.DataLabel
import java.io.File
import edu.gemini.fits.{Hedit, Header}
import collection.JavaConversions._
import edu.gemini.aspen.gds.api.Predef._

/**
 * Class that can take an existing file and add the headers passed in the constructor
 *
 * The FitsUpdater must preserve all the existing data and only add or update the headers
 * passed at construction
 */
class FitsUpdater(path: File, dataLabel: DataLabel, headers: List[Header]) {
    require(path.exists)
    require(path.isDirectory)
    require(dataLabel != null)

    def updateFitsHeaders() {
        val destinationFile = copyOriginal

        val hEdit = new Hedit(destinationFile)
        val updatedHeaders = headers sortBy { _.getIndex }

        updatedHeaders map { h => hEdit.updateHeader(getUpdatedKeywords(h), h.getIndex) }
    }

    private def getUpdatedKeywords(header: Header) = {
        val keywords = header.getKeywords.toList
        keywords.flatMap { header.getAll }
    }

    private def copyOriginal = {
        val originalFile = new File(path, toFits(dataLabel))
        val destinationFile = new File(path, newFitsName(dataLabel))

        destinationFile.createNewFile

        copy(originalFile, destinationFile)
        destinationFile
    }

    def toFits(dataLabel: DataLabel) = dataLabel.toString + ".fits"

    def newFitsName(dataLabel: DataLabel) = "N-" + toFits(dataLabel)
}