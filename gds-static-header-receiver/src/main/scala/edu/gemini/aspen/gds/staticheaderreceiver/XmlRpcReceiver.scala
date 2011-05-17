package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.gds.api.Conversions._

/**
 * XMLRPC server, forwards calls to a singleton actor, needed because this class
 * is instantiated by the XMLRPC library, so we cannot pass parameters to it.
 */
class XmlRpcReceiver {
  //todo: check if it is possible to expose a method that returns nothing.
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