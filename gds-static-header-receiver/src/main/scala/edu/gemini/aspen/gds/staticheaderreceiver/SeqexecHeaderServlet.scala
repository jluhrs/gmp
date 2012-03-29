package edu.gemini.aspen.gds.staticheaderreceiver

import scala.collection._
import scala.collection.JavaConversions._
import org.apache.xmlrpc.webserver.XmlRpcServlet
import org.ops4j.pax.web.service.WebContainer
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase
import org.apache.xmlrpc.server.{RequestProcessorFactoryFactory, XmlRpcHandlerMapping, PropertyHandlerMapping}
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory.RequestProcessorFactory
import org.apache.xmlrpc.XmlRpcRequest
import org.apache.felix.ipojo.handlers.event.Publishes
import org.apache.felix.ipojo.handlers.event.publisher.Publisher

@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[HeaderReceiver]))
class SeqexecHeaderServlet(@Requires keywordsDatabase: TemporarySeqexecKeywordsDatabase,
                           @Requires programIdDB: ProgramIdDatabase,
                           @Requires webContainer: WebContainer) extends XmlRpcServlet with HeaderReceiver {
  val initParams = mutable.Map("enabledForExtensions" -> "true")
  // Register XMLRPC Handler
  webContainer.registerServlet(this, Array("/xmlrpc/*"), initParams, null)
  @Publishes(name = "SeqexecHeaderServlet", topics = "edu/gemini/aspen/gds/obsevent/handler", dataKey = "observationevent")
  var publisher: Publisher = _

  def this(keywordsDatabase: TemporarySeqexecKeywordsDatabase,
           programIdDB: ProgramIdDatabase,
           webContainer: WebContainer,
           publisher0: Publisher = null) {
    this(keywordsDatabase, programIdDB, webContainer)
    publisher = publisher0
  }

  override def newXmlRpcHandlerMapping(): XmlRpcHandlerMapping = {
    val phm = new PropertyHandlerMapping()
    phm.setVoidMethodEnabled(true)
    phm.setRequestProcessorFactoryFactory(new XmlRpcReceiverProcessFactoryFactory())
    phm.addHandler("HeaderReceiver", classOf[XmlRpcReceiver])
    phm
  }

  @Validate
  def start() {
  }

  @Invalidate
  def stop() {
  }

  /**
   * XMLRPC Process factories */
  class XmlRpcReceiverProcessFactoryFactory extends RequestProcessorFactoryFactory {
    def getRequestProcessorFactory(c: Class[_]) = new XmlRpcReceiverProcessFactory
  }

  class XmlRpcReceiverProcessFactory extends RequestProcessorFactory {
    def getRequestProcessor(r: XmlRpcRequest) = new XmlRpcReceiver(keywordsDatabase, programIdDB, publisher)
  }

}