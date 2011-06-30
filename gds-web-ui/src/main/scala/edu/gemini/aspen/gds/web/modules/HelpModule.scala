package edu.gemini.aspen.gds.web.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.gds.web.api.GDSWebModule

@Component
@Instantiate
@Provides(specifications = Array(classOf[GDSWebModule]))
class HelpModule extends GDSWebModule {
    val title = "Help"
}