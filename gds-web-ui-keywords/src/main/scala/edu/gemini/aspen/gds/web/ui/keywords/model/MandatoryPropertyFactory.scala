package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api.{Mandatory, GDSConfiguration}
import com.vaadin.ui.CheckBox

/**
 * PropertyItemWrapperFactory for Mandatory that uses a CheckBox
 */
class MandatoryPropertyFactory extends PropertyItemWrapperFactory(classOf[Mandatory], classOf[CheckBox]) {
  override val width = 40

  override val title = "Mand."

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val checkBox = new CheckBox("", config.mandatory.mandatory)
    checkBox.setCaption("Mandatory")
    checkBox.setRequired(true)
    checkBox.setStyleName("mandatory-keyword")

    def updateFunction(config: GDSConfiguration): GDSConfiguration = {
      config.copy(mandatory = Mandatory(checkBox.getValue.asInstanceOf[Boolean]))
    }

    (checkBox, updateFunction)
  }
}
