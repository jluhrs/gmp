package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.gds.api.Conversions._
import java.util.logging.{Level, Logger}
import edu.gemini.aspen.gds.keywords.database.{StoreProgramId, ProgramIdDatabase}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Store
import org.apache.felix.ipojo.handlers.event.publisher.Publisher
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}

class XmlRpcReceiver(keywordsDatabase: TemporarySeqexecKeywordsDatabase, programIdDB: ProgramIdDatabase, publisher: Publisher) {
  protected val LOG = Logger.getLogger(this.getClass.getName)


  /**
   * Opening an observation means associating it with a program ID, and telling GDS to wait for
   * someone to close it before writing the FITS file.
   *
   * @param programId The ID of the program in the ODB that specifies this observation
   * @param dataLabel The name of the FITS file to be written
   */
  def openObservation(programId: String, dataLabel: String) {
    LOG.info(s"Opened Observation, Program ID: $programId Data label: $dataLabel")
    programIdDB ! StoreProgramId(dataLabel, programId)
    publisher.sendData(ObservationEvent.EXT_START_OBS, new DataLabel(dataLabel))
  }

  /**
   * Convenience method to save on XMLRPC calls
   *
   * @param programId The ID of the program in the ODB that specifies this observation
   * @param dataLabel The name of the FITS file to be written, and to which keywords must be associated
   * @param keywords An array of Strings, each of which contains three parts, separated by a comma: the keyword name, the data type and the value
   */
  def openObservation(programId: String, dataLabel: String, keywords: Array[Object]) {
    openObservation(programId, dataLabel)
    storeKeywords(dataLabel, keywords)
  }

  /**
   * Closing an observation means all keywords have been sent, so the FITS file may be updated
   *
   * @param dataLabel The name of the FITS file to be written
   */
  def closeObservation(dataLabel: String) {
    LOG.info(s"Closed Observation, Data label: $dataLabel")
    publisher.sendData(ObservationEvent.EXT_END_OBS, new DataLabel(dataLabel))
  }

  /**
   * Convenience method to save on XMLRPC calls
   *
   * @param dataLabel The name of the FITS file to be written, and to which keywords must be associated
   * @param keywords An array of Strings, each of which contains three parts, separated by a comma: the keyword name, the data type and the value
   */
  def closeObservation(dataLabel: String, keywords: Array[Object]) {
    storeKeywords(dataLabel, keywords)
    closeObservation(dataLabel)
  }

  /**
   * Associate a keyword to a given data set
   *
   * @param dataLabel The name of the FITS file to be written, and to which keywords must be associated
   * @param keyword The FITS keyword name
   * @param value The FITS keyword value
   */
  def storeKeyword(dataLabel: String, keyword: String, value: String) {
    LOG.info(s"Data label: $dataLabel Keyword: $keyword Value: $value")
    keywordsDatabase ! Store(dataLabel, keyword, value)
  }

  /**
   * Associate a keyword to a given data set
   *
   * @param dataLabel The name of the FITS file to be written, and to which keywords must be associated
   * @param keyword The FITS keyword name
   * @param value The FITS keyword value
   */
  def storeKeyword(dataLabel: String, keyword: String, value: Double) {
    LOG.info(s"Data label: $dataLabel Keyword: $keyword Value: $value")
    keywordsDatabase ! Store(dataLabel, keyword, value.asInstanceOf[AnyRef])
  }

  /**
   * Associate a keyword to a given data set
   *
   * @param dataLabel The name of the FITS file to be written, and to which keywords must be associated
   * @param keyword The FITS keyword name
   * @param value The FITS keyword value
   */
  def storeKeyword(dataLabel: String, keyword: String, value: Int) {
    LOG.info(s"Data label: $dataLabel Keyword: $keyword Value: $value")
    keywordsDatabase ! Store(dataLabel, keyword, value.asInstanceOf[AnyRef])
  }

  /**
   * Associate keywords to a given data set
   *
   * @param dataLabel The name of the FITS file to be written, and to which keywords must be associated
   * @param keywords An array of Strings, each of which contains three parts, separated by a comma: the keyword name, the data type and the value
   */
  def storeKeywords(dataLabel: String, keywords: Array[Object]) {
    for {
      keyword <- keywords
      pieces = keyword.toString.split(",")
    } {
      val key = pieces(0).trim().toUpperCase
      val dataType = pieces(1).trim()
      try {
        val value = if (pieces.length == 3) pieces(2).trim() else ""

        dataType match {
          case "INT"    => storeKeyword(dataLabel, key, value.toInt)
          case "DOUBLE" => storeKeyword(dataLabel, key, value.toDouble)
          case "STRING" => storeKeyword(dataLabel, key, value.toString)
          case x        => LOG.severe(s"Wrong data type: $x")
        }
      } catch {
        case ex: java.lang.NumberFormatException => LOG.log(Level.SEVERE, ex.getMessage, ex)
        case e: Exception                        => LOG.log(Level.SEVERE, e.getMessage, e)
      }
    }
  }
}

