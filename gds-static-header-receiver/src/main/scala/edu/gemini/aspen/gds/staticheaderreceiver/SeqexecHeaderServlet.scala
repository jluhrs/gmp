package edu.gemini.aspen.gds.staticheaderreceiver

import scala.collection.JavaConversions._
import org.apache.xmlrpc.webserver.XmlRpcServlet
import org.apache.xmlrpc.server.{XmlRpcHandlerMapping, PropertyHandlerMapping}
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Component}
import org.ops4j.pax.web.service.WebContainer

@Component
@Instantiate
class SeqexecHeaderServlet(@Requires webContainer:WebContainer) extends XmlRpcServlet {
  val initParams = scala.collection.mutable.Map("enabledForExtensions" -> "true")
  webContainer.registerServlet(this, Array("/xmlrpc/*"), initParams, null)

  override def newXmlRpcHandlerMapping():XmlRpcHandlerMapping = {
    val phm = new PropertyHandlerMapping()
    phm.addHandler("HeaderReceiver", classOf[XmlRpcReceiver])
    phm
  }

}