package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.gds.api.Conversions._
import java.util.logging.{Level, Logger}
import edu.gemini.aspen.gds.keywords.database.{StoreProgramId, ProgramIdDatabase}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Store

class XmlRpcReceiver(keywordsDatabase: TemporarySeqexecKeywordsDatabase, programIdDB: ProgramIdDatabase) {
  protected val LOG = Logger.getLogger(this.getClass.getName)

  def initObservation(programId: String, dataLabel: String) {
    LOG.info("Program ID: " + programId + " Data label: " + dataLabel)
    programIdDB ! StoreProgramId(dataLabel, programId)
  }

  def storeKeyword(dataLabel: String, keyword: String, value: String) {
    LOG.info("Data label: " + dataLabel + " Keyword: " + keyword + " Value: " + value)
    keywordsDatabase ! Store(dataLabel, keyword, value)
  }

  def storeKeyword(dataLabel: String, keyword: String, value: Double) {
    LOG.info("Data label: " + dataLabel + " Keyword: " + keyword + " Value: " + value)
    keywordsDatabase ! Store(dataLabel, keyword, value.asInstanceOf[AnyRef])
  }

  def storeKeyword(dataLabel: String, keyword: String, value: Int) {
    LOG.info("Data label: " + dataLabel + " Keyword: " + keyword + " Value: " + value)
    keywordsDatabase ! Store(dataLabel, keyword, value.asInstanceOf[AnyRef])
  }

  def storeKeywords(dataLabel: String, keywords: Array[Object]) {
    for (keyword <- keywords) {
      val pieces = keyword.asInstanceOf[String].split(",")
      val key = pieces(0).trim()
      val dataType = pieces(1).trim()
      val value = pieces(2).trim()
      try {
        dataType match {
          case "INT" => storeKeyword(dataLabel, key, value.toInt)
          case "DOUBLE" => storeKeyword(dataLabel, key, value.toDouble)
          case "STRING" => storeKeyword(dataLabel, key, value.toString)
          case x => LOG.severe("Wrong data type: " + x)
        }
      } catch {
        case ex: java.lang.NumberFormatException => LOG.log(Level.SEVERE, ex.getMessage, ex)
      }
    }
  }
}

