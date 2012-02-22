package edu.gemini.aspen.gds.fits

import edu.gemini.aspen.giapi.data.DataLabel
import java.io.File
import edu.gemini.fits.{Hedit, Header}
import collection.JavaConversions._
import edu.gemini.aspen.gds.api.Predef._
import actors.Reactor
import java.util.logging.{Level, Logger}

case class Update(namingFunction: DataLabel => String = {
  label => FitsUpdater.toFitsFileName(label)
})

/**
 * Class that can take an existing file and add the headers passed in the constructor
 *
 * The FitsUpdater must preserve all the existing data and only add or update the headers
 * passed at construction
 */
class FitsUpdater(fromDirectory: File, toDirectory: File, dataLabel: DataLabel, headers: List[Header]) extends Reactor[Update] {
  protected val LOG = Logger.getLogger(this.getClass.getName)
  require(fromDirectory.exists, {
    LOG.severe("Directory " + fromDirectory + " doesn't exist")
    "Directory " + fromDirectory + " doesn't exist"
  })
  require(fromDirectory.isDirectory, {
    LOG.severe(fromDirectory + " is not a directory")
    fromDirectory + " is not a directory"
  })
  if (!toDirectory.exists) {
    toDirectory.mkdirs()
  }
  require(toDirectory.exists, {
    LOG.severe("Directory " + toDirectory + " doesn't exist")
    "Directory " + toDirectory + " doesn't exist"
  })
  require(toDirectory.isDirectory, {
    LOG.severe(toDirectory + " is not a directory")
    toDirectory + " is not a directory"
  })
  require(dataLabel != null, {
    LOG.severe("Datalabel is null")
    "Datalabel is null"
  })
  start()

  def act() {
    loop {
      react {
        case Update(namingFunction) => {
          try {
            updateFitsHeaders(namingFunction)
          } catch {
            case ex =>
              LOG.log(Level.WARNING, ex.getMessage, ex)
          }
        }
      }
    }
  }

  /**
   * Updates the headers in the destination file, adding to the current set of
   * headers the ones passed in the constructor
   *
   * @param namingFunction, it is a an optional method to name the new file. It is a relative name, not absolute. For example: {label => "N-" + label.getName + ".fits"}
   */
  def updateFitsHeaders(namingFunction: DataLabel => String = FitsUpdater.toFitsFileName) {
    val originalFile = new File(fromDirectory, FitsUpdater.toFitsFileName(dataLabel))
    val destinationFile = new File(toDirectory, namingFunction(dataLabel))
    copy(originalFile, destinationFile)

    val hEdit = new Hedit(destinationFile)
    val updatedHeaders = headers sortBy {
      _.getIndex
    }

    updatedHeaders filter {
      // Skip headers not found in the file
      _.getIndex < hEdit.readAllHeaders().size()
    } map {
      h => hEdit.updateHeader(getUpdatedKeywords(h), h.getIndex)
    }

  }

  private def getUpdatedKeywords(header: Header) = {
    val keywords = header.getKeywords.toList
    keywords.flatMap {
      header.getAll
    }
  }
}

object FitsUpdater {
  def toFitsFileName(dataLabel: DataLabel) = dataLabel.toString + ".fits"
}