package edu.gemini.aspen.gds.web.ui.logs

import edu.gemini.aspen.gds.web.ui.api.GDSWebModuleFactory
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}

/**
 * Factory component of LogsModule */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[GDSWebModuleFactory]))
class LogsModuleFactory(@Requires logSource: LogSource) extends GDSWebModuleFactory {
  override def buildWebModule = new LogsModule(logSource)

  override protected def canEqual(other: Any): Boolean = other.isInstanceOf[LogsModuleFactory]
}