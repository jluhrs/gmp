package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations.{Property, Provides, Component}
import io.Source
import java.io.{BufferedWriter, File, FileWriter}

/**
 * Reads and writes configuration files. Update operations(saveConfiguration and updateConfiguration) try to match
 * configuration elements by checking which element has the maximum number of columns identical to the new configuration
 * element. Ties are broken by prioritizing: Subsystem channel, Keyword Name and Comment. If there's still a tie, the
 * operation fails.
 */
trait GDSConfigurationService {
  /**
   * Reads from file and returns the configuration items
   */
  def getConfiguration: List[GDSConfiguration]

  def getConfigurationForUpdate: List[Option[ConfigItem[_]]]

  /**
   * Overwrites the current configuration file
   */
  def newConfiguration(config: List[GDSConfiguration]): Unit

  /**
   * Updates elements that already exist, adds the new ones, and deletes missing ones
   */
  def updateConfiguration(config: List[Option[ConfigItem[_]]]): Unit

  /**
   * Adds new configurations to the end of the file
   */
  def addConfiguration(config: List[GDSConfiguration]): Unit
}

abstract class ConfigType[T] {
  def ConfigTypeToString(item: ConfigItem[T]): String
}

object ConfigType {

  implicit object ConfigurationType extends ConfigType[GDSConfiguration] {

    override def ConfigTypeToString(item: ConfigItem[GDSConfiguration]): String = {
      item.value.formatForConfigFile
    }
  }

  implicit object CommentType extends ConfigType[Comment] {

    override def ConfigTypeToString(item: ConfigItem[Comment]): String = {
      item.value.toString
    }
  }

}

class ConfigItem[T](val value: T)(implicit val _type: ConfigType[T]) {
  override def toString: String = "ConfigItem(" + value.toString() + ")"

  override def equals(other: Any): Boolean = other match {
    case that: ConfigItem[_] => (that canEqual this) && value == that.value
    case _ => false
  }

  // Used by equals and can be overrode by extensions
  protected def canEqual(other: Any): Boolean = other.isInstanceOf[ConfigItem[_]]

  override def hashCode: Int = 41 * (41 + value.##)
}

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

@Component
@Provides(specifications = Array(classOf[GDSConfigurationService]))
class GDSConfigurationServiceImpl(@Property(name = "keywordsConfiguration", value = "NOVALID", mandatory = true) configurationFile: String) extends GDSConfigurationService {
  def getConfiguration: List[GDSConfiguration] = {
    val parser = new GDSConfigurationParser()
    val a = parser.parseFileRawResult(configurationFile)
    parser.parseFile(configurationFile) filter {
      _.isInstanceOf[GDSConfiguration]
    } map {
      _.asInstanceOf[GDSConfiguration]
    }
    GDSConfigurationFile.getConfiguration(configurationFile)
  }

  def newConfiguration(config: List[GDSConfiguration]) {
    val newFile = new File(configurationFile)
    val writer = new BufferedWriter(new FileWriter(newFile))
    for (configLine <- config) {
      writer.write(configLine.formatForConfigFile)
      writer.newLine()
    }
    writer.close()
  }

  def getConfigurationForUpdate: List[Option[ConfigItem[_]]] = {
    GDSConfigurationFile.getConfigurationForUpdate(configurationFile)
  }

  def updateConfiguration(config: List[Option[ConfigItem[_]]]) {
    GDSConfigurationFile.saveConfiguration(configurationFile, config)
  }

  def addConfiguration(config: List[GDSConfiguration]) {
    val newFile = new File(configurationFile)
    val writer = new BufferedWriter(new FileWriter(newFile, true))
    for (configLine <- config) {
      writer.append(configLine.formatForConfigFile)
      writer.newLine()
    }
    writer.close()
  }
}