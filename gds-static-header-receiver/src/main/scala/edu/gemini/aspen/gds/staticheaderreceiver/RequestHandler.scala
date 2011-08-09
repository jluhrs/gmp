package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}
import java.util.logging.Logger
import edu.gemini.aspen.gds.keywords.database.{StoreProgramId, ProgramIdDatabase}
import actors.Reactor
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Store

sealed abstract class RequestHandlerMessage

// message to initialize an observation. Program ID needed to fetch data from odb
case class InitObservation(programId: String, dataLabel: DataLabel) extends RequestHandlerMessage

// store a keyword/value pair for a given data label
case class StoreKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) extends RequestHandlerMessage

/**
 * Singleton actor that forwards messages from the XMLRPC server to the appropriate DB
 */
object RequestHandler extends Reactor[RequestHandlerMessage] {
  private val LOG = Logger.getLogger(this.getClass.getName)

  private var keywordsDatabase: TemporarySeqexecKeywordsDatabase = _
  private var programIdDB: ProgramIdDatabase = _

  def setDatabases(keywordsDatabase: TemporarySeqexecKeywordsDatabase, programIdDB: ProgramIdDatabase) {
    this.keywordsDatabase = keywordsDatabase
    this.programIdDB = programIdDB
  }

  def act() {
    loop {
      react {
        case InitObservation(programId, dataLabel) => initObservation(programId, dataLabel)
        case StoreKeyword(dataLabel, keyword, value) => storeKeyword(dataLabel, keyword, value)
        case _ => error("Argument not known")
      }
    }
  }

  def initObservation(programId: String, dataLabel: DataLabel) {
    LOG.info("Program ID: " + programId + " Data label: " + dataLabel)
    programIdDB ! StoreProgramId(dataLabel, programId)
  }

  def storeKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) {
    LOG.info("Data label: " + dataLabel + " Keyword: " + keyword + " Value: " + value)
    keywordsDatabase ! Store(dataLabel, keyword, value)
  }
}