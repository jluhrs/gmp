package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api._
import com.vaadin.data.util.ObjectProperty
import configuration.ConfigItem

/**
 * This class is a DataSource that is used for the Keywords Table Read-Only view
 */
class ReadOnlyGDSKeywordsDataSource(config: List[ConfigItem[_]]) extends GDSKeywordsDataSource(config) {
  {
    // Give each config an propertyId
    val indexedConfig = config.zipWithIndex

    // Add an item per
    indexedConfig filter {
      _._1.value.isInstanceOf[GDSConfiguration] //filter out comments and blank lines
    } map {
      case (c, i) => {
        val item = addItem(i)
        val data = configToItem(c.value.asInstanceOf[GDSConfiguration]) zip GDSKeywordsDataSource.displayedFields

        // Add one item per displayed field
        data map {
          case (v, p) => {
            item.getItemProperty(p.getType.getSimpleName).setValue(new ObjectProperty[String](v))
          }
        }
      }
    }
  }

  // The header is the same as the item
  override def propertyHeader(propertyId: String) = propertyId

  // Add each property as a String
  override protected[keywords] def addContainerProperties = {
    GDSKeywordsDataSource.displayedFields map {
      c => addContainerProperty(c.getType.getSimpleName, classOf[String], "")
    }
  }

  /**
   * Creates a row from a config item
   */
  private def configToItem(config: GDSConfiguration) = {
    List[String](config.instrument.name.toString,
      config.event.name.toString,
      config.keyword.key,
      config.index.index.toString,
      config.dataType.name.toString,
      config.mandatory.mandatory.toString,
      config.nullValue.value.toString,
      config.subsystem.name.toString,
      config.channel.name.toString,
      config.arrayIndex.value.toString,
      config.fitsComment.value.toString
    )
  }
}