package edu.gemini.aspen.gds.staticheaderreceiver

import org.apache.xmlrpc.server.{XmlRpcServer, PropertyHandlerMapping, XmlRpcServerConfigImpl}
import org.apache.xmlrpc.webserver.WebServer


/**
 * Constructs an XMLRPC server.
 */
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