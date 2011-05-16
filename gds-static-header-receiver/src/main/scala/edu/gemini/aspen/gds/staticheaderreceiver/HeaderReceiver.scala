package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.gds.keywords.database.{ProgramIdDatabaseImpl, ProgramIdDatabase}

case class InitObservation(programId: String, dataLabel: DataLabel)

case class StoreKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef)
case class RetrieveValue(dataLabel: DataLabel, keyword: FitsKeyword)
case class Clean(dataLabel:DataLabel)
case class CleanAll()
trait HeaderReceiver

@Component
@Instantiate
@Provides(specifications = Array(classOf[HeaderReceiver]))
class SeqexecHeaderReceiver(@Requires keywordsDatabase: TemporarySeqexecKeywordsDatabase, @Requires programIdDB:ProgramIdDatabase) {
  private val webServer = XmlRpcServerFactory.newServer("HeaderReceiver", classOf[XmlRpcReceiver], 12345)

  @Validate
  def start() {
    RequestHandler.setDatabases(keywordsDatabase,programIdDB)
    RequestHandler.start()
    webServer.start();
  }

  @Invalidate
  def shutdown() {
    webServer.shutdown()
  }

}

/**
 * Temporary test app
 */
object TestApp extends Application {
  org.apache.log4j.BasicConfigurator.configure();
  val seq = new SeqexecHeaderReceiver(new TemporarySeqexecKeywordsDatabaseImpl, new ProgramIdDatabaseImpl)
  seq.start()
  Thread.sleep(1000000)
}

