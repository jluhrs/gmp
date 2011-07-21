package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api.{GDSEvent, GDSConfiguration}
import com.vaadin.ui.NativeSelect
import scala.collection.JavaConversions._
import edu.gemini.aspen.giapi.data.ObservationEvent

/**
 * PropertyItemWrapperFactory for GDSEvent that uses a ComboBox to select a Observation Event
 */
class GDSEventPropertyFactory extends PropertyItemWrapperFactory(classOf[GDSEvent], classOf[NativeSelect]) {
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val select = new NativeSelect("", GDSEventPropertyFactory.obsEvents)
    select.setCaption("Observation Event")
    select.setRequired(true)
    select.setNullSelectionAllowed(false)
    select.select(config.event.name.replace("OBS_", ""))
    select.addStyleName("small-combobox")

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(select.getValue) map {
        v => config.copy(event = GDSEvent("OBS_" + v.toString))
      } getOrElse {
        config
      }
    }

    (select, wrapper)
  }
}

/**
 * Object to hold shared obsEvent variable
 */
object GDSEventPropertyFactory {
  // Name of the items in the select box
  val obsEvents = ObservationEvent.values().toList map {
    _.name.replace("OBS_", "")
  }
}