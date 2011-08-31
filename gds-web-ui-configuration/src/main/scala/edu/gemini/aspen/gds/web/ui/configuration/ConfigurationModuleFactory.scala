package edu.gemini.aspen.gds.web.ui.configuration

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import org.osgi.service.cm.ConfigurationAdmin
import edu.gemini.aspen.gmp.services.PropertyHolder


@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class ConfigurationModuleFactory(@Requires propHolder: PropertyHolder, @Requires configAdmin: ConfigurationAdmin) extends GDSWebModuleFactory {
  override def buildWebModule = new ConfigurationModule(propHolder, configAdmin)

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[ConfigurationModuleFactory]
}