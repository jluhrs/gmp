package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.{GDSEvent, GDSConfiguration}
import com.vaadin.ui.NativeSelect
import scala.collection.JavaConversions._
import edu.gemini.aspen.giapi.data.ObservationEvent

/**
 * PropertyItemWrapperFactory for GDSEvent that uses a ComboBox to select a Observation Event
 */
class GDSEventPropertyFactory extends PropertyItemWrapperFactory(classOf[GDSEvent], classOf[NativeSelect]) {
  val obsEvents = ObservationEvent.values().toList map {
    _.name.replace("OBS_", "")
  }

  override def createItemAndWrapper(config: GDSConfiguration) = {
    val comboBox = new NativeSelect("", obsEvents)
    comboBox.setCaption("Observation Event")
    comboBox.setRequired(true)
    comboBox.setNullSelectionAllowed(false)
    comboBox.select(config.event.name.replace("OBS_", ""))
    comboBox.addStyleName("small-combobox")

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(comboBox.getValue) map {
        v => config.copy(event = GDSEvent("OBS_" + v.toString))
      } getOrElse {
        config
      }
    }

    (comboBox, wrapper)
  }
}