package edu.gemini.aspen.gds.staticheaderreceiver

import scala.collection.JavaConversions._
import org.apache.xmlrpc.webserver.XmlRpcServlet
import org.apache.xmlrpc.server.{XmlRpcHandlerMapping, PropertyHandlerMapping}
import org.ops4j.pax.web.service.WebContainer
import org.apache.felix.ipojo.annotations.Provides._
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase

@Component
@Instantiate
@Provides(specifications = Array(classOf[HeaderReceiver]))
class SeqexecHeaderServlet(@Requires keywordsDatabase: TemporarySeqexecKeywordsDatabase, @Requires programIdDB: ProgramIdDatabase, @Requires webContainer: WebContainer) extends XmlRpcServlet with HeaderReceiver {
  val initParams = scala.collection.mutable.Map("enabledForExtensions" -> "true")
  webContainer.registerServlet(this, Array("/xmlrpc/*"), initParams, null)

  override def newXmlRpcHandlerMapping(): XmlRpcHandlerMapping = {
    val phm = new PropertyHandlerMapping()
    phm.setVoidMethodEnabled(true)
    phm.addHandler("HeaderReceiver", classOf[XmlRpcReceiver])
    phm
  }

  @Validate
  def start() {
    RequestHandler.setDatabases(keywordsDatabase, programIdDB)
    RequestHandler.start()
  }

  @Invalidate
  def stop() {
    // Do nothing as is not apparently possible to stop RequestHandler
  }
}