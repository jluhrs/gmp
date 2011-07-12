package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.{Instrument, GDSConfiguration}

/**
 * ConfigurationItemWrapperFactory for Instrument that is read only
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













