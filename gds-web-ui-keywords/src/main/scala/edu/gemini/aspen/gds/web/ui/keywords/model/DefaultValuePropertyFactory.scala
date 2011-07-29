package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{DefaultValue, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class DefaultValuePropertyFactory extends PropertyItemWrapperFactory(classOf[DefaultValue], classOf[TextField]) {
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.nullValue.value.toString)
    textField.addValidator(DefaultValuePropertyFactory.validator)
    textField.setCaption("Default Value")
    textField.setImmediate(true)
    textField.setRequired(true)
    textField.setMaxLength(80)
    textField.setInvalidAllowed(false)

    def updateFunction(config: GDSConfiguration) = {
      config.copy(nullValue = DefaultValue(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object DefaultValuePropertyFactory {
  val validator = new AbstractStringValidator("Value {0} must be less than 8 characters") {
    // todo verify max allowed length
    def isValidString(value: String) = value.length <= 80
  }
}

