package edu.gemini.aspen.gds.web.ui.keywords

import scala.Predef._
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.web.ui.api.VaadinUtilities._
import com.vaadin.data.util.IndexedContainer
import com.vaadin.data.Item
import com.vaadin.ui._
import edu.gemini.aspen.gds.api.{GDSEvent, Instrument, Mandatory, GDSConfiguration}
import edu.gemini.aspen.giapi.data.{FitsKeyword, ObservationEvent}

/**
 * This is a class that describes a cell in a table creating wrappers for each of them
 * so that they can be created and referred back
 *
 * There is a factory per column that represents a field
 *
 * Each column needs a title and a type
 */
class InstrumentConfigurationItemWrapperFactory extends ConfigurationItemWrapperFactory(classOf[Instrument], classOf[String]) {
  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    itemProperty(item).setValue(config.instrument.name)
    itemProperty(item).setReadOnly(true)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      config
    }

    wrapper
  }
}

























