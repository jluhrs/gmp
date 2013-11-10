package edu.gemini.cas.db

import xml.XML
import edu.gemini.epics.api.Channel
import edu.gemini.cas.impl.ChannelAccessServerImpl

/**
 * Parses an XML definition file and creates CAS-based channels
 */
class ChannelBuilder(filename: String) {
  private val src = XML.loadFile(filename)
  private val channelFactory = new ChannelAccessServerImpl
  channelFactory.start()

  val channels:Seq[Channel[_]] = src match {
    case <channels>{channels @ _*}</channels> => {
      for {channel @ <channel>{_*}</channel> <- channels
        name        = (channel \\ "name").head.text
        channelType = (channel \\ "type").head.text
        value       = (channel \\ "value").head.text
      } yield buildChannel(channelType, name, value)
    }
    case _ => sys.error("Bad format")
  }

  private def buildChannel(channelType:String, name: String, value: String): Channel[_] = channelType.toLowerCase match {
    case "string"  => channelFactory.createChannel[String](name, value)
    case "float"   => channelFactory.createChannel[Float](name, value.toFloat)
    case "double"  => channelFactory.createChannel[Double](name, value.toDouble)
    case "int"     => channelFactory.createChannel[Int](name, value.toInt)
    case "byte"    => channelFactory.createChannel[Byte](name, value.toByte)
    case _         => sys.error("Unknown channel type")
  }
}

