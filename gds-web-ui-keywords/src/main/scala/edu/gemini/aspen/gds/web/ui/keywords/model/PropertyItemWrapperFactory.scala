package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.GDSConfiguration

/**
 * This is a class that describes a cell in a table creating wrappers for each of them
 * so that they can be created and referred back
 *
 * There is a factory per column that represents a field
 *
 * Each column needs a title and a type
 */
abstract class PropertyItemWrapperFactory(fieldClass: Class[_], val columnType: Class[_]) {
  /**
   * Name of the column, by default the field class name
   */
  val title = fieldClass.getSimpleName

  /**
   * Width of the column, -1 by default
   */
  val width = -1

  /**
   * This is a factory method that creates a property item, typically a UI control
   * and a function wrapper that will take the UI control value back to the GDS Configuration
   *
   * The function returned should take an incoming GDSConfiguration and produced a new one with only the
   * value in the UI control modified
   */
  def createItemAndWrapper(config: GDSConfiguration, item: Item): (AnyRef, GDSKeywordsDataSource.WrappedConfigItem)

  /**
   * Populates a given item out of the created item
   *
   * Acts as a strategy pattern
   */
  def populateItem(config: GDSConfiguration, item: Item): GDSKeywordsDataSource.WrappedConfigItem = {
    val (value, wrapper) = createItemAndWrapper(config, item)
    item.getItemProperty(title).setValue(value)
    wrapper
  }

  // find the property in the item
  protected def itemProperty(item: Item) = item.getItemProperty(title)
}