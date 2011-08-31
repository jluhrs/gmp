package edu.gemini.aspen.gds.web.ui.configuration

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory


@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class ConfigurationModuleFactory extends GDSWebModuleFactory {
  override def buildWebModule = new ConfigurationModule

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[ConfigurationModuleFactory]
}