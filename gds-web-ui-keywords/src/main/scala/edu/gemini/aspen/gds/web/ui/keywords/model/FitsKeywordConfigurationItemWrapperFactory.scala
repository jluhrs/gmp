package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.giapi.data.FitsKeyword
import com.vaadin.ui.TextField

/**
 * ConfigurationItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class FitsKeywordConfigurationItemWrapperFactory extends ConfigurationItemWrapperFactory(classOf[FitsKeyword], classOf[TextField]) {
  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val textField = new TextField("", config.keyword.getName)
    itemProperty(item).setValue(textField)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      config.copy(keyword = new FitsKeyword(textField.getValue.toString))
    }

    wrapper
  }
}













