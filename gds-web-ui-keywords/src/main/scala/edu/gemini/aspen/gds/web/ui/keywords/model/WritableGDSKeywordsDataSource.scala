package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api._
import configuration.{Comment, ConfigItem}
import scala.collection.JavaConversions._
import com.vaadin.data.Item

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class WritableGDSKeywordsDataSource(config: List[Option[ConfigItem[_]]]) extends GDSKeywordsDataSource(config) {
  // Contains a list of configurations and wrappers for the UI items
  var configWrapper = {
    // Give each config an propertyId
    val indexedConfig = config.zipWithIndex

    // Add an item per
    indexedConfig filter {
      _._1.isDefined //filter out blank lines
    } filter {
      _._1.get.value.isInstanceOf[GDSConfiguration] //filter out comments
    } map {
      case (c, i) => {
        val item = addItem(i)
        // Add one itemProperty per displayed field
        (i, c.get.value.asInstanceOf[GDSConfiguration], addItemWrapper(c.get.value.asInstanceOf[GDSConfiguration], item))
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
    } map {
      f =>
        columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType)).title
    } getOrElse (propertyId)
  }

  override protected[keywords] def toGDSConfiguration: List[Option[ConfigItem[_]]] = {
    (config.zipWithIndex map {
      case (opt: Some[ConfigItem[_]], index) => opt.get match {
        case confItem: ConfigItem[_] => confItem.value match {
          case _: GDSConfiguration => {
            //if it's a GDSConfiguration
            configWrapper find {
              case (i, c, itemWrappers) => (i == index && containsId(index))
            } map {
              case (i, c, itemWrappers) => new ConfigItem(GDSKeywordsDataSource.itemToGDSConfiguration(c, itemWrappers)) //replace it with the new one
            }
          }
          case x: Comment => Some(new ConfigItem(x)) //if it's a comment, leave it be
        }
      }
      case (None, index) => None //if it's a blank line, leave it be
    }) ++ (for ((i, c, _) <- configWrapper if (i >= config.size)) yield Some(new ConfigItem(c)))
  }

  override def removeItem(itemId: AnyRef): Boolean = {
    configWrapper = configWrapper filterNot {
      case (l, _, _) => itemId == l
    }
    super.removeItem(itemId)
  }

  override protected[keywords] def addNewConfig(conf: GDSConfiguration) {
    val item = addItemAfter(lastItemId, last)
    configWrapper = ((last, conf, addItemWrapper(conf, item)) :: configWrapper).reverse
    last += 1
  }

  private def addItemWrapper(c: GDSConfiguration, item: Item): List[GDSKeywordsDataSource.ConfigUpdateFunction] = {
    GDSKeywordsDataSource.displayedFields map {
      f => {
        val cd = columnsDefinitions.getOrElse(f.getType, defaultColumnDefinition(f.getType))
        cd.populateItem(c, item)
      }
    }
  }
}