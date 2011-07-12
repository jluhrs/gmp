package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.{Mandatory, GDSConfiguration}
import com.vaadin.ui.CheckBox

/**
 * PropertyItemWrapperFactory for Mandatory that uses a CheckBox
 */
class MandatoryPropertyItemWrapperFactory extends PropertyItemWrapperFactory(classOf[Mandatory], classOf[CheckBox]) {
  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val checkBox = new CheckBox("", !config.mandatory.mandatory)
    item.getItemProperty(title).setValue(checkBox)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      config.copy(mandatory = Mandatory(checkBox.getValue == "1"))
    }

    (checkBox, wrapper)
  }
}













