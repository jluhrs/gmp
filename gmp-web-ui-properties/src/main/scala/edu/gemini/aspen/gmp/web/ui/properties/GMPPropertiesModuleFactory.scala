package edu.gemini.aspen.gmp.web.ui.properties

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gmp.services.PropertyHolder

/**
 * Factory for the GMPPropertiesModule
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class GMPPropertiesModuleFactory(@Requires propertyHolder: PropertyHolder) extends GDSWebModuleFactory {
  override def buildWebModule = new GMPPropertiesModule(propertyHolder)

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[GMPPropertiesModuleFactory]
}
