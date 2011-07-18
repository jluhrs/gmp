package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.util.IndexedContainer
import com.vaadin.data.Item
import edu.gemini.aspen.giapi.data.{FitsKeyword}
import edu.gemini.aspen.gds.api._
import com.vaadin.ui.Button

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class GDSKeywordsDataSource(config: List[GDSConfiguration]) extends IndexedContainer {

  // Contains the non default column definitions
  val columnsDefinitions = Map[Class[_], PropertyItemWrapperFactory](
    classOf[Instrument] -> new InstrumentPropertyItemWrapperFactory,
    classOf[GDSEvent] -> new GDSEventPropertyFactory,
    classOf[FitsKeyword] -> new FitsKeywordPropertyFactory,
    classOf[HeaderIndex] -> new HeaderIndexPropertyFactory,
    classOf[DataType] -> new DataTypePropertyFactory,
    classOf[Mandatory] -> new MandatoryPropertyFactory,
    classOf[DefaultValue] -> new DefaultValuePropertyFactory,
    classOf[Subsystem] -> new SubsystemPropertyFactory,
    classOf[Channel] -> new ChannelPropertyFactory,
    classOf[ArrayIndex] -> new ArrayIndexPropertyFactory,
    classOf[FitsComment] -> new FitsCommentPropertyFactory
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
        val itemWrappers = GDSKeywordsDataSource.displayedFields map {
          f => {
            val cd = columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType))
            cd.populateItem(c, item)
          }
        }
        (i, c, itemWrappers)
      }
    }
  }

  def defaultColumnDefinition(clazz: Class[_]) = new DefaultPropertyItemWrapperFactory(clazz)

  /**
   * Adds the container properties, i.e. the columns on the table
   */
  protected[keywords] def addContainerProperties = {
    GDSKeywordsDataSource.displayedFields map {
      c => {
        val cd = columnsDefinitions.getOrElse(c.getType, defaultColumnDefinition(c.getType))
        addContainerProperty(cd.title, cd.columnType, "")
      }
    }
  }

  /**
   * Returns a list of GDSConfiguration based of the originally passed but updated with the changes from the GUI
   */
  def toGDSConfiguration: List[GDSConfiguration] = {
    configWrapper map {
      case (i, c, itemWrappers) => {
        GDSKeywordsDataSource.itemToGDSConfiguration(c, itemWrappers)
      }
    } toList
  }

}

object GDSKeywordsDataSource {
  type WrappedConfigItem = (GDSConfiguration) => GDSConfiguration

  /**
   * Uses the wrapped functions to recursively get a new GDSConfiguration based on the actual values from the UI
   */
  protected[keywords] def itemToGDSConfiguration(config: GDSConfiguration, functions: List[WrappedConfigItem]): GDSConfiguration = {
    functions match {
      case List() => config
      case List(x) => x(config)
      case x :: rest => x(itemToGDSConfiguration(config, rest))
    }
  }

  /**
   * Returns a list of the fields of GDSConfiguration we are interested to display/edit
   */
  protected[keywords] def displayedFields =
    classOf[GDSConfiguration].getDeclaredFields.toList take (11)
}