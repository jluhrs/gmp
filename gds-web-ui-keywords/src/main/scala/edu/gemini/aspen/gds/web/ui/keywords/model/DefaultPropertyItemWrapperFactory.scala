package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api.GDSConfiguration

/**
 * Default implementation of PropertyItemWrapperFactory
 *
 * Should disappear
 */
class DefaultPropertyItemWrapperFactory(clazz: Class[_]) extends PropertyItemWrapperFactory(clazz, classOf[String]) {
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    null
  }
}
