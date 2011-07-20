package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api._
import scala.collection.JavaConversions._

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class WritableGDSKeywordsDataSource(config: List[GDSConfiguration]) extends GDSKeywordsDataSource(config) {

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
  override protected[keywords] def addContainerProperties = {
    GDSKeywordsDataSource.displayedFields map {
      c => {
        val cd = columnsDefinitions.getOrElse(c.getType, defaultColumnDefinition(c.getType))
        addContainerProperty(cd.title, cd.columnType, "")
      }
    }
  }

  override def toGDSConfiguration: List[GDSConfiguration] = {
    configWrapper collect {
      case (i, c, itemWrappers) if containsId(i) => {
        GDSKeywordsDataSource.itemToGDSConfiguration(c, itemWrappers)
      }
    } toList
  }

}