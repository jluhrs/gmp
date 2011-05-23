package edu.gemini.aspen.gds.fits

import edu.gemini.aspen.giapi.data.DataLabel
import java.io.{IOException, FileInputStream, FileOutputStream, File}
import edu.gemini.fits.{Hedit, Header}
import collection.JavaConversions._

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
        val updatedHeaders = headers sortBy {
            h => h.getIndex
        }

        for (h <- updatedHeaders) {
            hEdit.updateHeader(getUpdatedKeywords(h), h.getIndex)
        }
    }

    private def getUpdatedKeywords(header: Header) = {
        val keywords = header.getKeywords.toList
        keywords.flatMap {
            h => header.getAll(h)
        }
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

    // TODO: Move these two methods to a common utility class
    @throws(classOf[IOException])
    def copy(from: File, to: File) {
        use(new FileInputStream(from)) {
            in => use(new FileOutputStream(to)) {
                out =>
                    out getChannel () transferFrom (
                            in getChannel, 0, Long.MaxValue)
            }
        }
    }

    private def use[T <: {def close() : Unit}](closable: T)(block: T => Unit) {
        try {
            block(closable)
        } finally {
            closable.close()
        }
    }
}