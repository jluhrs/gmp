package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService

/**
 * Factory for the KeywordsTableModule
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[GDSWebModuleFactory]))
class KeywordsTableModuleFactory(@Requires configService: GDSConfigurationService) extends GDSWebModuleFactory {
  override def buildWebModule = new KeywordsTableModule(configService)

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[KeywordsTableModuleFactory]
}
