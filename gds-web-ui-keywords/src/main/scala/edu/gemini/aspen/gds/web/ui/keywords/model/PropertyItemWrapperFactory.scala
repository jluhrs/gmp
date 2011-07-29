package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.GDSConfiguration
import com.vaadin.ui.Component

/**
 * This is a class that describes a cell in a table creating wrappers for each of them
 * so that they can be created and referred back
 *
 * There is a factory per column that represents a field
 *
 * Each column needs a title and a type
 */
abstract class PropertyItemWrapperFactory(val fieldClass: Class[_], val columnType: Class[_]) {
  /**
   * Name of the column, by default the field class name
   */
  val title = fieldClass.getSimpleName

  /**
   * Width of the column, -1 by default
   */
  val width = -1

  /**
   * This is a factory method that creates a builds a controller that can handle a single property
   * item af a configuration. It will return a UI control and a function wrapper
   * that will take the value in the UI control back to the GDS Configuration
   *
   * The function returned should take an incoming GDSConfiguration and produced a new one with only the
   * value in the UI control modified. The function will typically be a closure that can read the GUI control
   * value
   */
  def buildPropertyControlAndWrapper(config: GDSConfiguration): (Component, GDSKeywordsDataSource.ConfigUpdateFunction)

  /**
   * Populates a given item out of the created item
   *
   * Acts as a strategy pattern
   */
  def populateItem(config: GDSConfiguration, item: Item): GDSKeywordsDataSource.ConfigUpdateFunction = {
    val (value, wrapper) = buildPropertyControlAndWrapper(config)
    item.getItemProperty(fieldClass.getSimpleName).setValue(value)
    wrapper
  }

}