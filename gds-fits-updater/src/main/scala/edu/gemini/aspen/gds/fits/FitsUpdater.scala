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

    /**
     * Updates the headers in the destination file, adding to the current set of
     * headers the ones passed in the constructor
     *
     * @param namingFunction, it is a an optional method to rename the file to a new name
     */
    def updateFitsHeaders(namingFunction: DataLabel => File = newFitsFile) {
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
        val originalFile = new File(path, toFitsFileName(dataLabel))
        val destinationFile = newFitsFile(dataLabel)

        copy(originalFile, destinationFile)
    }

    def toFitsFileName(dataLabel: DataLabel) = dataLabel.toString + ".fits"

    def newFitsFile(dataLabel: DataLabel) = new File(path, "N-" + toFitsFileName(dataLabel))
}