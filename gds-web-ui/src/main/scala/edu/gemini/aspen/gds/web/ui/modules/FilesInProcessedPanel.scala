package edu.gemini.aspen.gds.web.ui.modules

import org.apache.felix.ipojo.annotations.{Provides, Instantiate}
import edu.gemini.aspen.gds.web.ui.api.StatusPanelModule
import java.lang.Thread
import java.util.concurrent.TimeUnit
import collection.immutable.Range.Int
import com.vaadin.data.util.ObjectProperty

/**
 * Status panel that can show the amount of files processed
 */
@org.apache.felix.ipojo.annotations.Component
@Instantiate
@Provides(specifications = Array(classOf[StatusPanelModule]))
class FilesInProcessedPanel extends AbstractStatusPanelModule {
  val order = 1
  val label = "Files In Process"
  val property = new ObjectProperty[String]("20")
  val item = "2"
  var value = 0;

  new Thread(new Runnable() {
    def run() {
      for {x <- List.range(1, 1000)} {
        value += 1
        property.setValue(value)
        TimeUnit.SECONDS.sleep(1)
      }
    }
  }).start()
}



