package edu.gemini.aspen.gds.staticheaderreceiver

import scala.collection.JavaConversions._
import org.apache.xmlrpc.webserver.XmlRpcServlet
import org.ops4j.pax.web.service.WebContainer
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase
import org.apache.xmlrpc.server.{RequestProcessorFactoryFactory, XmlRpcHandlerMapping, PropertyHandlerMapping}
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory.RequestProcessorFactory
import org.apache.xmlrpc.XmlRpcRequest

@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[HeaderReceiver]))
class SeqexecHeaderServlet(@Requires keywordsDatabase: TemporarySeqexecKeywordsDatabase, @Requires programIdDB: ProgramIdDatabase, @Requires webContainer: WebContainer) extends XmlRpcServlet with HeaderReceiver {
  val initParams = Map("enabledForExtensions" -> "true")
  val requestHandler = new RequestHandler(keywordsDatabase, programIdDB)
  // Register XMLRPC Handler
  webContainer.registerServlet(this, Array("/xmlrpc/*"), initParams, null)

  override def newXmlRpcHandlerMapping(): XmlRpcHandlerMapping = {
    val phm = new PropertyHandlerMapping()
    phm.setVoidMethodEnabled(true)
    phm.setRequestProcessorFactoryFactory(new XmlRpcReceiverProcessFactoryFactory())
    phm.addHandler("HeaderReceiver", classOf[XmlRpcReceiver])
    phm
  }

  @Validate
  def start() {
    requestHandler.start()
  }

  @Invalidate
  def stop() {
    requestHandler ! ExitRequestHandler()
  }

  /**
   * XMLRPC Process factories */
  class XmlRpcReceiverProcessFactoryFactory extends RequestProcessorFactoryFactory {
    def getRequestProcessorFactory(c: Class[_]) = new XmlRpcReceiverProcessFactory
  }

  class XmlRpcReceiverProcessFactory extends RequestProcessorFactory {
    def getRequestProcessor(r: XmlRpcRequest) = new XmlRpcReceiver(requestHandler)
  }

}