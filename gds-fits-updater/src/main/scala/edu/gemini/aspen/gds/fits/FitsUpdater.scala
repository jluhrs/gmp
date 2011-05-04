package edu.gemini.aspen.gds.fits

import edu.gemini.aspen.giapi.data.DataLabel
import java.io.{IOException, FileInputStream, FileOutputStream, File}
import edu.gemini.fits.{Hedit, Header}
import collection.JavaConversions._

/**
 *
 */

class FitsUpdater(path: File, dataLabel: DataLabel, headers: List[Header]) {
    require(path.exists)
    require(path.isDirectory)
    require(dataLabel != null)

    def updateFitsHeaders() {
        val dest = copyOriginal

        val hEdit = new Hedit(dest)
        val primaryHeader = headers(0)
        val hdrs = primaryHeader.getKeywords
        println("Update primary of " + dest)
        hdrs foreach {
            h =>
                hEdit.updatePrimary(primaryHeader.getAll(h))
        }
    }

    private def copyOriginal = {
        val originalFile = new File(path, dataLabel.toString + ".fits")
        val destinationFile = new File(path, "N-" + dataLabel.toString + ".fits")

        destinationFile.createNewFile

        copy(originalFile, destinationFile)
        destinationFile
    }

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