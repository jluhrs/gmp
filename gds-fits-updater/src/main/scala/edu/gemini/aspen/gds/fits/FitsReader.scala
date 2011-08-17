package edu.gemini.aspen.gds.fits

import java.io.File
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.Hedit
import scala.collection.JavaConversions._
import collection.immutable.Set
import java.util.logging.Logger

class FitsReader(file: File) {
  protected val LOG = Logger.getLogger(this.getClass.getName)
  require(file.exists, {
    LOG.severe("File " + file + " doesn't exist")
    "File " + file + " doesn't exist"
  })
  require(file.isFile, {
    LOG.severe("File " + file + " is not a file")
    "File " + file + " is not a file"
  })
  require(file.canRead, {
    LOG.severe("Cannot read file " + file)
    "Cannot read file " + file
  })
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