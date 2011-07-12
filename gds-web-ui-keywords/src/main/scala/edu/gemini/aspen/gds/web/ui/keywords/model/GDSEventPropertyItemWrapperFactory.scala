package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.{GDSEvent, GDSConfiguration}
import com.vaadin.ui.NativeSelect
import scala.collection.JavaConversions._
import edu.gemini.aspen.giapi.data.ObservationEvent

/**
 * PropertyItemWrapperFactory for GDSEvent that uses a ComboBox to select a Observation Event
 */
class GDSEventPropertyItemWrapperFactory extends PropertyItemWrapperFactory(classOf[GDSEvent], classOf[NativeSelect]) {
  val obsEvents = ObservationEvent.values().toList map {
    _.name
  }

  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val comboBox = new NativeSelect("", obsEvents)
    comboBox.select(config.event.name)

    item.getItemProperty(title).setValue(comboBox)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(comboBox.getValue) map {
        v => config.copy(event = GDSEvent(v.toString))
      } getOrElse {
        config
      }
    }

    (comboBox, wrapper)
  }
}













