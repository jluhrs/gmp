package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api._
import scala.collection.JavaConversions._
import com.vaadin.data.Item

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class WritableGDSKeywordsDataSource(config: List[GDSConfiguration]) extends GDSKeywordsDataSource(config) {
  // Contains a list of configurations and wrappers for the UI items
  var configWrapper = {
    // Give each config an propertyId
    val indexedConfig = config.zipWithIndex

    // Add an item per
    indexedConfig map {
      case (c, i) => {
        val item = addItem(i)
        // Add one itemProperty per displayed field
        (i, c, addItemWrapper(c, item))
      }
    }
  }

  def defaultColumnDefinition(clazz: Class[_]) = new DefaultPropertyItemWrapperFactory(clazz)

  /**
   * Adds the container properties, i.e. the columns on the table
   */
  override protected[keywords] def addContainerProperties = {
    GDSKeywordsDataSource.displayedFields map {
      c => {
        val cd = columnsDefinitions.getOrElse(c.getType, defaultColumnDefinition(c.getType))
        addContainerProperty(cd.fieldClass.getSimpleName, cd.columnType, "")
      }
    }
  }

  override protected[keywords] def propertyHeader(propertyId: String) = {
    GDSKeywordsDataSource.displayedFields find {
      _.getType.getSimpleName == propertyId
    } map { f =>
      columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType)).title
    } getOrElse (propertyId)
  }

  override protected[keywords] def toGDSConfiguration: List[GDSConfiguration] = {
    configWrapper collect {
      case (i, c, itemWrappers) if containsId(i) => {
        GDSKeywordsDataSource.itemToGDSConfiguration(c, itemWrappers)
      }
    } toList
  }

  override protected[keywords] def addNewConfig(config: GDSConfiguration) {
    val i = size
    val item = addItemAfter(lastItemId, i)
    configWrapper = ((i, config, addItemWrapper(config, item)) :: configWrapper).reverse
  }

  private def addItemWrapper(c: GDSConfiguration, item: Item):List[GDSKeywordsDataSource.ConfigUpdateFunction] = {
    GDSKeywordsDataSource.displayedFields map {
      f => {
        val cd = columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType))
        cd.populateItem(c, item)
      }
    }
  }
}