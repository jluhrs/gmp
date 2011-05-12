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

object RequestHandler extends Actor {
  var keywordsDatabase: KeywordsDatabase = _

  def setDatabase(keywordsDatabase: KeywordsDatabase) {
    this.keywordsDatabase = keywordsDatabase
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
    println("Program ID: "+programId+" Data label: "+dataLabel)
    //send this information to somebody
  }

  def storeKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) {
    println("Data label: "+dataLabel+" Keyword: "+keyword+" Value: "+value)
    keywordsDatabase ! Store(dataLabel, CollectedValue(keyword, value, "", 0))
  }
}

class XmlRpcReceiver {
  def initObservation(programId: String, dataLabel: String): Boolean = {
    RequestHandler ! InitObservation(programId, dataLabel)
    true
  }

  def storeKeyword(dataLabel: String, keyword: String, value: String): Boolean = {
    RequestHandler ! StoreKeyword(dataLabel, keyword, value)
    true
  }

  def storeKeyword(dataLabel: String, keyword: String, value: Double): Boolean = {
    RequestHandler ! StoreKeyword(dataLabel, keyword, value.asInstanceOf[AnyRef])
    true
  }

  def storeKeyword(dataLabel: String, keyword: String, value: Int): Boolean = {
    RequestHandler ! StoreKeyword(dataLabel, keyword, value.asInstanceOf[AnyRef])
    true
  }
}


object XmlRpcServerFactory {
  def newServer(serverName: String, serverClass: Class[_], port: Int): WebServer = {
    val webServer = new WebServer(port);
    val xmlRpcServer: XmlRpcServer = webServer.getXmlRpcServer();
    val phm: PropertyHandlerMapping = new PropertyHandlerMapping();
    phm.addHandler(serverName, serverClass);
    xmlRpcServer.setHandlerMapping(phm);
    val serverConfig: XmlRpcServerConfigImpl = xmlRpcServer.getConfig().asInstanceOf[XmlRpcServerConfigImpl];
    serverConfig.setEnabledForExtensions(true);
    serverConfig.setContentLengthOptional(false);
    webServer
  }

}

object Test extends Application {
  org.apache.log4j.BasicConfigurator.configure();
  val seq = new SeqexecHeaderReceiver(new KeywordsDatabaseImpl)
  seq.start
  Thread.sleep(1000000)
}