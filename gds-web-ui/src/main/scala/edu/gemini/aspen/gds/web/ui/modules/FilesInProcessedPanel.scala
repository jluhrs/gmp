package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate}
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule

/**
 * Status panel that can show the amount of files processed
 */
@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class FilesInProcessedPanel extends AbstractStatusPanelModule {
    val label = "Files In Process"
    val item = "2"

}



