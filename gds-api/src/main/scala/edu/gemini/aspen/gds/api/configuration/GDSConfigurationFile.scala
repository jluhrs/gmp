package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.gds.api.GDSConfiguration
import java.io.{FileWriter, BufferedWriter, File}

/**
 * This object provides utility methods to manipulate a configuration file
 */
object GDSConfigurationFile {
  def getConfiguration(configurationFile: String): List[GDSConfiguration] = {
    val contents: List[Option[ConfigItem[_]]] = new GDSConfigurationParser().parseFileRawResult(configurationFile).get map {
      case Some(x: GDSConfiguration) => Some(new ConfigItem(x))
      case Some(x: Comment) => Some(new ConfigItem(x))
      case None => None
    }
    getConfiguration(contents)
  }

  def getConfiguration(contents: List[Option[ConfigItem[_]]]): List[GDSConfiguration] = {
    contents collect {
      case Some(x) => x
    } filter {
      _.isInstanceOf[ConfigItem[_]]
    } map {
      _.value
    } filter {
      _.isInstanceOf[GDSConfiguration]
    } map {
      _.asInstanceOf[GDSConfiguration]
    }
  }

  def getConfigurationForUpdate(configurationFile: String): List[Option[ConfigItem[_]]] = {
    new GDSConfigurationParser().parseFileRawResult(configurationFile).get map {
      case Some(x: GDSConfiguration) => Some(new ConfigItem(x))
      case Some(x: Comment) => Some(new ConfigItem(x))
      case None => None
    }
  }

  def saveConfiguration(configurationFile: String, contents: List[Option[ConfigItem[_]]]) {
    val newFile = new File(configurationFile)
    val writer = new BufferedWriter(new FileWriter(newFile))
    for (configLine <- contents) {
      configLine map {
        case x => writer.write(x._type.ConfigTypeToString(x))
      }
      writer.newLine()
    }
    writer.close()
  }
}