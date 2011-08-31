package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api._
import configuration.{ConfigItem, ConfigType, BlankLine}
import scala.collection.JavaConversions._
import com.vaadin.data.Item

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
class WritableGDSKeywordsDataSource(config: List[ConfigItem[_]]) extends GDSKeywordsDataSource(config) {
  // Contains a list of configurations and wrappers for the UI items
  var configWrapper = {
    // Give each config an propertyId
    val indexedConfig = config.zipWithIndex

    // Add an item per
    indexedConfig filter {
      _._1.value.isInstanceOf[GDSConfiguration] //filter out comments and blank lines
    } map {
      case (c, i) => {
        val item = addItem(i)
        // Add one itemProperty per displayed field
        (i, c.value.asInstanceOf[GDSConfiguration], addItemWrapper(c.value.asInstanceOf[GDSConfiguration], item))
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

  override protected[keywords] def toGDSConfiguration: List[ConfigItem[_]] = {
    val modifiedItemList: List[ConfigItem[_]] = config.zipWithIndex map {
      case (confItem: ConfigItem[_], index: Int) => {
        if (confItem._type == ConfigType.ConfigurationType) {
          //if it's a GDSConfiguration, replace modified items
          configWrapper find {
            case (i, c, itemWrappers) => (i == index && containsId(index))
          } map {
            case (i, c, itemWrappers) => new ConfigItem(GDSKeywordsDataSource.itemToGDSConfiguration(c, itemWrappers)) //replace it with the new one
          } getOrElse {
            new ConfigItem(new BlankLine) //if item was deleted, replace with blank line
          }
        }
        else if (confItem._type == ConfigType.CommentType) {
          confItem //if it's a comment, leave it be
        }
        else if (confItem._type == ConfigType.BlankLineType) {
          confItem //if it's a blank line, leave it be
        }
      }.asInstanceOf[ConfigItem[_]]
    }
    modifiedItemList ++ (for ((i, c, _) <- configWrapper if (i >= config.size)) yield new ConfigItem(c))
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