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

  /**
   * Overwrites the current configuration file
   */
  def newConfiguration(config: List[GDSConfiguration]): Unit

  /**
   * Updates elements that already exist, adds the new ones, and deletes missing ones
   */
  def saveConfiguration(config: List[GDSConfiguration]): Unit

  /**
   * Updates the given configuration items
   */
  def updateConfiguration(config: List[GDSConfiguration]): Unit

  /**
   * Adds new configurations to the end of the file
   */
  def addConfiguration(config: List[GDSConfiguration]): Unit
}

@Component
@Provides(specifications = Array(classOf[GDSConfigurationService]))
class GDSConfigurationServiceImpl(@Property(name = "keywordsConfiguration", value = "NOVALID", mandatory = true) configurationFile: String) extends GDSConfigurationService {
  def getConfiguration: List[GDSConfiguration] = {
    new GDSConfigurationParser().parseFile(configurationFile) filter {
      _.isInstanceOf[GDSConfiguration]
    } map {
      _.asInstanceOf[GDSConfiguration]
    }
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

  def saveConfiguration(config: List[GDSConfiguration]) {} //todo

  def updateConfiguration(config: List[GDSConfiguration]) {} //todo

  def addConfiguration(config: List[GDSConfiguration]) {} //todo
}