package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.GDSConfiguration

/**
 * Default implementation of ConfigurationItemWrapperFactory
 *
 * Should disappear
 */
class DefaultConfigurationItemWrapperFactory(clazz: Class[_]) extends ConfigurationItemWrapperFactory(clazz, classOf[String]) {
  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    null
  }
}













