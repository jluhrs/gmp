package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate}
import edu.gemini.aspen.gds.web.ui.api.{StatusPanelModule}
import com.vaadin.data.util.ObjectProperty

/**
 * Status panel that can show the total amount of files processed
 */
@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class FilesProcessedPanel extends AbstractStatusPanelModule {
  val order = 2
  val label = "Files Processed"
  val item = "1234"
  val property = new ObjectProperty[String]("1234")
}



