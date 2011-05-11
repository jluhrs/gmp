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

trait HeaderReceiver

@Component
@Instantiate
@Provides(specifications = Array(classOf[HeaderReceiver]))
class SeqexecHeaderReceiver(@Requires keywordsDatabase: KeywordsDatabase) {

  @Validate
  def start() {
    RequestHandler.setDatabase(keywordsDatabase)
    val rpcServ = new RpcServer

    rpcServ.addServer("HeaderReceiver", classOf[XmlRpcReceiver])
    rpcServ.startServer(12345)
  }

  @Invalidate
  def shutdown() {
  }

}

object RequestHandler extends Actor {
  var keywordsDatabase:KeywordsDatabase = _

  def setDatabase(keywordsDatabase: KeywordsDatabase) {
    this.keywordsDatabase = keywordsDatabase
    start()

  }


  def act() {
    loop {
      react {
        case InitObservation(programId, dataLabel) => initObservation(programId, dataLabel)
        case StoreKeyword(dataLabel, keyword, value) => storeKeyword(dataLabel, keyword, value)
        case x: Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }

  def initObservation(programId: String, dataLabel: DataLabel) {
    //send this information to somebody
  }

  def storeKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) {
    keywordsDatabase ! Store(dataLabel, CollectedValue(keyword, value, "", 0))

  }
}

class XmlRpcReceiver {
  def initObservation(programId: String, dataLabel: String): Boolean = {
    RequestHandler ! InitObservation(programId,dataLabel)
    true
  }

  def storeStringKeyword(dataLabel: String, keyword: String, value: String): Boolean = {
    RequestHandler ! StoreKeyword(dataLabel,keyword,value)
    true
  }

  def storeDoubleKeyword(dataLabel: String, keyword: String, value: Double): Boolean = {
    RequestHandler ! StoreKeyword(dataLabel,keyword,value)
    true
  }

  def storeIntKeyword(dataLabel: String, keyword: String, value: Int): Boolean = {
    RequestHandler ! StoreKeyword(dataLabel,keyword,value)
    true
  }
}


class RpcServer {

  val phm: PropertyHandlerMapping = new PropertyHandlerMapping();

  def addServer(serverName: String, serverClass: Class[_]) {


    phm.addHandler(serverName, serverClass);

  }

  def startServer(port: Int) {
    val webServer = new WebServer(port);
    val xmlRpcServer: XmlRpcServer = webServer.getXmlRpcServer();
    xmlRpcServer.setHandlerMapping(phm);
    val serverConfig: XmlRpcServerConfigImpl = xmlRpcServer.getConfig().asInstanceOf[XmlRpcServerConfigImpl];
    serverConfig.setEnabledForExtensions(true);
    serverConfig.setContentLengthOptional(false);
    webServer.start();
  }

}

object Test extends Application {
  org.apache.log4j.BasicConfigurator.configure();
  val seq = new SeqexecHeaderReceiver(new KeywordsDatabaseImpl)
  seq.start
}