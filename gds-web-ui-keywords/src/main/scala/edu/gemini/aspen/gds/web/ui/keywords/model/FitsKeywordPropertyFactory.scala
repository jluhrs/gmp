package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.giapi.data.FitsKeyword
import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class FitsKeywordPropertyFactory extends PropertyItemWrapperFactory(classOf[FitsKeyword], classOf[TextField]) {
  val validator = new AbstractStringValidator("Value {0} must be less than 8 characters") {
    def isValidString(value: String) = value.length <= 9
  }

  override def createItemAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.keyword.getName)
    textField.addValidator(validator)
    textField.setCaption("FITS Keyword")
    textField.setRequired(true)
    textField.addValidator(validator)
    textField.setImmediate(true)
    textField.setRequired(true)
    textField.setMaxLength(8)
    textField.setInvalidAllowed(false)

    def wrapper(config: GDSConfiguration) = {
      config.copy(keyword = new FitsKeyword(textField.getValue.toString))
    }

    (textField, wrapper)
  }
}













