package edu.gemini.aspen.gds.web.ui.keywords

import edu.gemini.aspen.gds.web.ui.api.GDSWebModule
import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}

@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[GDSWebModule]))
class KeywordsTableModule extends GDSWebModule {
    val title = "Keyword Configuration"
    val order = 0
}