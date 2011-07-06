package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory

/**
 * Factory for the HelpModule
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class HelpModuleFactory extends GDSWebModuleFactory {
  override def buildWebModule = new HelpModule

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[HelpModuleFactory]

}

