package edu.gemini.aspen.gds.web.ui.modules

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import org.apache.felix.ipojo.annotations.{Instantiate, Provides, Component}

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

