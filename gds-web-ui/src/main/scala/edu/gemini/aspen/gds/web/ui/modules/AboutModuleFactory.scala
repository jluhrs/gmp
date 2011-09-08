package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory

/**
 * Factory for the AboutModule
 */
@Component
//@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class AboutModuleFactory extends GDSWebModuleFactory {
  override def buildWebModule = new AboutModule

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[AboutModuleFactory]

}

