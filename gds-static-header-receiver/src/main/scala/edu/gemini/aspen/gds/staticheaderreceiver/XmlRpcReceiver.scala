package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.gds.api.Conversions._


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