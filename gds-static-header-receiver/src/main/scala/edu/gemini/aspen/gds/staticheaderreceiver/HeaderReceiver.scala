package edu.gemini.aspen.gds.staticheaderreceiver

import org.apache.xmlrpc.server.{XmlRpcServer, PropertyHandlerMapping, XmlRpcServerConfigImpl}
import org.apache.xmlrpc.webserver.WebServer
import actors.Actor
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectedValue
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywords.database.{KeywordsDatabaseImpl, Store, KeywordsDatabase}
import edu.gemini.aspen.gds.api.Conversions._

case class InitObservation(programId: String, dataLabel: DataLabel)

case class StoreKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef)
case class RetrieveValue(dataLabel: DataLabel, keyword: FitsKeyword)
case class Clean(dataLabel:DataLabel)
case class CleanAll()
trait HeaderReceiver

@Component
@Instantiate
@Provides(specifications = Array(classOf[HeaderReceiver]))
class SeqexecHeaderReceiver(@Requires keywordsDatabase: TemporarySeqexecKeywordsDatabase) {
  val webServer = XmlRpcServerFactory.newServer("HeaderReceiver", classOf[XmlRpcReceiver], 12345)

  @Validate
  def start() {
    RequestHandler.setDatabase(keywordsDatabase)
    RequestHandler.start()
    webServer.start();
  }

  @Invalidate
  def shutdown() {
    webServer.shutdown()
  }

}


object TestApp extends Application {
  org.apache.log4j.BasicConfigurator.configure();
  val seq = new SeqexecHeaderReceiver(new TemporarySeqexecKeywordsDatabaseImpl)
  seq.start
  Thread.sleep(1000000)
}

