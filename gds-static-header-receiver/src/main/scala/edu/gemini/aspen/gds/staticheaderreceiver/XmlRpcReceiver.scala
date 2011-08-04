package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.gds.api.Conversions._
import scala.collection.JavaConversions._
import java.util.logging.{Level, Logger}

case class IntKeyword(keyword: String, value: Int)

/**
 * XMLRPC server, forwards calls to a singleton actor, needed because this class
 * is instantiated by the XMLRPC library, so we cannot pass parameters to it.
 */
class XmlRpcReceiver {
  protected val LOG = Logger.getLogger(this.getClass.getName)

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

  def storeKeywords(dataLabel: String, keywords: Array[Object]): Boolean = {
    for (keyword <- keywords) {
      val pieces = keyword.asInstanceOf[String].split(",")
      val key = pieces(0)
      val dataType = pieces(1)
      val value = pieces(2)
      try {
        dataType match {
          case "INT" => RequestHandler ! StoreKeyword(dataLabel, key, value.toInt.asInstanceOf[AnyRef])
          case "DOUBLE" => RequestHandler ! StoreKeyword(dataLabel, key, value.toDouble.asInstanceOf[AnyRef])
          case "STRING" => RequestHandler ! StoreKeyword(dataLabel, key, value.toString.asInstanceOf[AnyRef])
          case x => LOG.severe("Wrong data type: " + x)
        }
      } catch {
        case ex: java.lang.NumberFormatException => LOG.log(Level.SEVERE, ex.getMessage, ex)
      }
    }
    true
  }
}

