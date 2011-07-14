package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import com.vaadin.ui.NativeSelect
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.api.{Subsystem, KeywordSource, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for GDSEvent that uses a ComboBox to select a Observation Event
 */
class SubsystemPropertyFactory extends PropertyItemWrapperFactory(classOf[Subsystem], classOf[NativeSelect]) {
  val subSystems = KeywordSource.values.toList takeWhile {
    _ != KeywordSource.NONE
  } map {
    _.toString
  }

  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val comboBox = new NativeSelect("", subSystems)
    comboBox.setNullSelectionAllowed(false)
    comboBox.select(config.subsystem.name.toString)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(comboBox.getValue) map {
        v => config.copy(subsystem = Subsystem(KeywordSource.withName(v.toString)))
      } getOrElse {
        config
      }
    }

    (comboBox, wrapper)
  }
}













