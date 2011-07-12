package edu.gemini.aspen.gds.web.ui.keywords

import scala.Predef._
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.web.ui.api.VaadinUtilities._
import com.vaadin.data.util.IndexedContainer
import com.vaadin.data.Item
import com.vaadin.ui._
import edu.gemini.aspen.gds.api.{GDSEvent, Instrument, Mandatory, GDSConfiguration}
import edu.gemini.aspen.giapi.data.{FitsKeyword, ObservationEvent}

/**
 * This is a class that describes a cell in a table creating wrappers for each of them
 * so that they can be created and referred back
 *
 * There is a factory per column that represents a field
 *
 * Each column needs a title and a type
 */
abstract class ConfigurationItemWrapperFactory(fieldClass: Class[_], val columnType: Class[_]) {
  /**
   * Name of the column, by default the field class name
   */
  def title = fieldClass.getSimpleName

  /**
   * This is a factory method that creates a property item, typically a UI control
   * and a function wrapper that will ctake the UI control value back to the GDS Configuration
   *
   * The function returned should take an incoming GDSConfiguration and produced a new one with only the
   * value in the UI control modified
   */
  def createItemAndWrapper(config: GDSConfiguration, item: Item): (GDSConfiguration) => GDSConfiguration

  // find the property in the item
  protected def itemProperty(item: Item) = item.getItemProperty(title)
}

object GDSKeywordsDataSource {
  type WrappedConfigItem = (GDSConfiguration) => GDSConfiguration
}

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class GDSKeywordsDataSource(config: List[GDSConfiguration]) extends IndexedContainer {

  // Contains the non default column definitions
  val columnsDefinitions = Map[Class[_], ConfigurationItemWrapperFactory](
    classOf[Instrument] -> new InstrumentConfigurationItemWrapperFactory,
    classOf[GDSEvent] -> new GDSEventConfigurationItemWrapperFactory,
    classOf[FitsKeyword] -> new FitsKeywordConfigurationItemWrapperFactory
  )

  addContainerProperties

  // Contains a list of configurations and wrappers for the UI items
  val configWrapper = {
    // Give each config an id
    val indexedConfig = config.zipWithIndex

    // Add an item per
    indexedConfig map {
      case (c, i) => {
        val item = addItem(i)
        // Add one itemProperty per displayed field
        val itemWrappers = displayedFields map {
          f =>
            val cd = columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType))
            cd.createItemAndWrapper(c, item)
        }
        (i, c, itemWrappers)
      }
    }
  }

  def defaultColumnDefinition(clazz: Class[_]) = new DefaultConfigurationItemWrapperFactory(clazz)

  /**
   * Adds the container properties, i.e. the columns on the table
   */
  protected[keywords] def addContainerProperties = {
    displayedFields map {
      c => {
        val cd = columnsDefinitions.getOrElse(c.getType, defaultColumnDefinition(c.getType))
        addContainerProperty(cd.title, cd.columnType, "")
      }
    }
  }

  /**
   * Returns a list of the fields of GDSConfiguration we are interested to display/edit
   */
  protected[keywords] def displayedFields =
    classOf[GDSConfiguration].getDeclaredFields.toList take (3)

  /**
   * Uses the wrapped functions to recursively get a new GDSConfiguration based on the actual values from the UI
   */
  protected[keywords] def itemToGDSConfiguration(config: GDSConfiguration, functions: List[GDSKeywordsDataSource.WrappedConfigItem]): GDSConfiguration = {
    functions match {
      case List() => config
      case List(x) => x(config)
      case x :: rest => x(itemToGDSConfiguration(config, rest))
    }
  }

  /**
   * Returns a list of GDSConfiguration based of the originally passed but updated with the changes from the GUI
   */
  def toGDSConfiguration: List[GDSConfiguration] = {
    configWrapper map {
      case (i, c, itemWrappers) => {
        itemToGDSConfiguration(c, itemWrappers)
      }
    } toList
  }

}













