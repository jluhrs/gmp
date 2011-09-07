package edu.gemini.aspen.gds.staticheaderreceiver

import scala.collection.JavaConversions._
import org.apache.xmlrpc.webserver.XmlRpcServlet
import org.apache.xmlrpc.server.{XmlRpcHandlerMapping, PropertyHandlerMapping}
import org.ops4j.pax.web.service.WebContainer
import org.apache.felix.ipojo.annotations.{Provides, Requires, Instantiate, Component}
import org.apache.felix.ipojo.annotations.Provides._

@Component
@Instantiate
@Provides(specifications = Array(classOf[HeaderReceiver]))
class SeqexecHeaderServlet(@Requires webContainer:WebContainer) extends XmlRpcServlet with HeaderReceiver {
  val initParams = scala.collection.mutable.Map("enabledForExtensions" -> "true")
  webContainer.registerServlet(this, Array("/xmlrpc/*"), initParams, null)

  override def newXmlRpcHandlerMapping():XmlRpcHandlerMapping = {
    val phm = new PropertyHandlerMapping()
    phm.addHandler("HeaderReceiver", classOf[XmlRpcReceiver])
    phm
  }

}