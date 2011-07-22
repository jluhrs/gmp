package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.ui.NativeSelect
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.api.{Subsystem, KeywordSource, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for GDSEvent that uses a ComboBox to select a Observation Event
 */
class SubsystemPropertyFactory extends PropertyItemWrapperFactory(classOf[Subsystem], classOf[NativeSelect]) {
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val select = new NativeSelect("", SubsystemPropertyFactory.subSystems)
    select.setNullSelectionAllowed(false)
    select.setCaption("Source")
    select.setRequired(true)
    select.select(config.subsystem.name.toString)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(select.getValue) map {
        v => config.copy(subsystem = Subsystem(KeywordSource.withName(v.toString)))
      } getOrElse {
        config
      }
    }

    (select, wrapper)
  }
}

object SubsystemPropertyFactory {
  val subSystems = KeywordSource.values.toList takeWhile {
    _ != KeywordSource.NONE
  } map {
    _.toString
  }
}

