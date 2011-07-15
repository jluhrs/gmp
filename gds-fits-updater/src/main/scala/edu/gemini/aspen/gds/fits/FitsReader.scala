package edu.gemini.aspen.gds.fits

import java.io.File
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.Hedit
import scala.collection.JavaConversions._
import collection.immutable.Set

class FitsReader(file: File) {
    require(file.exists)
    require(file.isFile)
    require(file.canRead)
    val hEdit = new Hedit(file)

    /**
     * Gets an immutable Set with the FitsKeywords present in the primary extension
     */
    def getKeywords(): Set[FitsKeyword] = {
        hEdit.readPrimary().getKeywords filterNot {
            _.isEmpty
        } map {
            new FitsKeyword(_)
        } toSet
    }

    /**
     * Gets an immutable Set with the FitsKeywords present in the given extension
     */
    def getKeywords(header: Int): Set[FitsKeyword] = {
        if (header >= 0) {
            header match {
                case 0 => getKeywords()
                case _ => {
                    val headers = hEdit.readAllHeaders()
                    if (header < headers.size()) {
                        headers.get(header).getKeywords filterNot {
                            _.isEmpty
                        } map {
                            new FitsKeyword(_)
                        } toSet
                    } else {
                        Set.empty[FitsKeyword]
                    }
                }
            }
        } else {
            Set.empty[FitsKeyword]
        }
    }
}