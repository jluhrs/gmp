package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModuleFactory]))
class LogsModuleFactory extends GDSWebModuleFactory {
    override def buildWebModule = new LogsModule

    override protected def canEqual(other: Any): Boolean = other.isInstanceOf[LogsModuleFactory]
}