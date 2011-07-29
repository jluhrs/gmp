package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api.{Instrument, GDSConfiguration}
import com.vaadin.ui.Label

/**
 * PropertyItemWrapperFactory for Instrument that is read only
 */
class InstrumentPropertyFactory extends PropertyItemWrapperFactory(classOf[Instrument], classOf[Label]) {
  override val width = 30

  override val title = "Inst."

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val label = new Label(config.instrument.name)
    label.setCaption("Instrument")

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      config
    }

    (label, wrapper)
  }
}
