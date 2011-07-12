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