package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.{Mandatory, GDSConfiguration}
import com.vaadin.ui.CheckBox

/**
 * PropertyItemWrapperFactory for Mandatory that uses a CheckBox
 */
class MandatoryPropertyFactory extends PropertyItemWrapperFactory(classOf[Mandatory], classOf[CheckBox]) {
  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val checkBox = new CheckBox("", config.mandatory.mandatory)
    checkBox.setStyleName("mandatory-keyword")

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      config.copy(mandatory = Mandatory(checkBox.getValue.asInstanceOf[Boolean]))
    }

    (checkBox, wrapper)
  }
}
