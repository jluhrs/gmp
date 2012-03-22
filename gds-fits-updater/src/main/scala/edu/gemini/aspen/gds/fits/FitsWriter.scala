package edu.gemini.aspen.gds.fits

import edu.gemini.aspen.gds.api.fits.Header
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.api.Conversions._
import com.google.common.base.Stopwatch
import nom.tam.util.BufferedFile
import java.io.File
import annotation.tailrec
import nom.tam.fits.{FitsException, BasicHDU, HeaderCard}
import com.google.common.io.Files

/**
 * Wrapper class that can write fits files
 *
 * @param file Name of the file
 */
class FitsWriter(file: File) extends FitsReader(file) {
  traverseHeaders(0)

  private def addNewKeywords(hdu: BasicHDU, header: Header) {
    val hduHeader = hdu.getHeader
    val existingKeywords = hduHeader.iterator() collect {
      case k: HeaderCard if k.isKeyValuePair => k
    }

    // Find keys in header but not in the file
    val newKeywords = header.keywords collect {
      // Select keys
      case k if !(existingKeywords exists {
        _.getKey == k.keywordName.key
      }) => k
    }
    LOG.info("Updating FITS file %s, header id:%d, with %d new keywords".format(file, header.index, newKeywords.size))
    newKeywords foreach {
      k => k.value match {
        case s: String => hduHeader.addValue(k.keywordName, s, k.comment)
        case i: Int => hduHeader.addValue(k.keywordName, i, k.comment)
        case d: Double => hduHeader.addValue(k.keywordName, d, k.comment)
        case _ => LOG.warning("Ignored key of unknown type " + k)
      }
    }
    // This call is needed to force fits file to read until the end of the
    // data section or the write may fail
    Option(hdu.getData) map {
      _.getData
    }
  }

  private def commitChanges(destinationFile: File) {
    val stopwatch = new Stopwatch().start()

    Files.createParentDirs(destinationFile)
    val finalFile = new BufferedFile(destinationFile, "rw")
    fitsFile.write(finalFile)

    LOG.info("File %s, written in %d [ms]".format(destinationFile, stopwatch.stop().elapsedMillis()))
  }

  /**
   * Updates the FITS file with new keywords add to the header
   * In case the header does not exist the operation is ignored
   * Existing keywords are not updated */
  def updateHeader(header: Header, destinationFile: File) {
    Option(fitsFile.getHDU(header.index)) foreach {
      hdu =>
        addNewKeywords(hdu, header)
        commitChanges(destinationFile)
    }
  }

  /**
   * Updates the FITS file with new keywords added to several headers at a time
   * In case the header does not exist the operation is ignored
   * Existing keywords are not updated */
  def updateHeaders(headers: Traversable[Header], destinationFile: File) {
    headers foreach {
      h => if (fitsFile.getHDU(h.index) != null) addNewKeywords(fitsFile.getHDU(h.index), h)
    }
    commitChanges(destinationFile)
  }

  @tailrec
  private def traverseHeaders(index: Int) {
    if (fitsFile.getHDU(index) != null) {
      traverseHeaders(index + 1)
    }
  }

}