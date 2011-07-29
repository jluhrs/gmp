package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api._
import com.vaadin.data.util.ObjectProperty

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class ReadOnlyGDSKeywordsDataSource(config: List[GDSConfiguration]) extends GDSKeywordsDataSource(config) {
  // Columns
  addContainerProperties

  // Data
  val configWrapper = {
    // Give each config an id
    val indexedConfig = config.zipWithIndex

    // Add an item per
    indexedConfig map {
      case (c, i) => {
        val item = addItem(i)
        val data = configToItem(c) zip GDSKeywordsDataSource.displayedFields

        // Add one item per displayed field
        data map {
          case (v, p) => {
            item.getItemProperty(p.getType.getSimpleName).setValue(new ObjectProperty[String](v))
          }
        }
      }
    }
  }

  override def propertyHeader(propertyId: String) = propertyId

  def configToItem(config: GDSConfiguration) = {
    List[String](config.instrument.name.toString,
      config.event.name.toString,
      config.keyword.getName.toString,
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

  /**
   * Adds the container properties, i.e. the columns on the table
   */
  override protected[keywords] def addContainerProperties = {
    GDSKeywordsDataSource.displayedFields map {
      c => {
        addContainerProperty(c.getType.getSimpleName, classOf[String], "")
      }
    }
  }

}