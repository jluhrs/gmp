package edu.gemini.aspen.gds.staticheaderreceiver

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywords.database.impl.ProgramIdDatabaseImpl
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase

/**
 * Needed by iPojo
 */
trait HeaderReceiver

/**
 * Component that starts the XMLRPC server and starts the actor that forwards messages to the appropriate database
 */
@Component
//@Instantiate
@Provides(specifications = Array(classOf[HeaderReceiver]))
class SeqexecHeaderReceiver(@Requires keywordsDatabase: TemporarySeqexecKeywordsDatabase, @Requires programIdDB: ProgramIdDatabase) extends HeaderReceiver {
  private val webServer = XmlRpcServerFactory.newServer("HeaderReceiver", classOf[XmlRpcReceiver], 12345)

  @Validate
  def start() {
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
object TestApp extends App {
  org.apache.log4j.BasicConfigurator.configure();
  val seq = new SeqexecHeaderReceiver(new TemporarySeqexecKeywordsDatabaseImpl, new ProgramIdDatabaseImpl)
  seq.start()
  Thread.sleep(1000000)
}

