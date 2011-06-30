package edu.gemini.aspen.gds.web.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.api.GDSWebModule
import scala.Predef._

@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[GDSWebModule]))
class HelpModule extends GDSWebModule {
    val title = "GDS Help"
    val order = 1
}