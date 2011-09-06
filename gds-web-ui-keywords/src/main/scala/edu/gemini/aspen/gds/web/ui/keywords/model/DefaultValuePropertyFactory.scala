package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{DefaultValue, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class DefaultValuePropertyFactory extends PropertyItemWrapperFactory(classOf[DefaultValue], classOf[TextField]) {
  override val width = 150
  
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.nullValue.value.toString)
    textField.addValidator(DefaultValuePropertyFactory.validator)
    textField.setCaption("Default Value")
    textField.setImmediate(true)
    textField.setRequired(true)
    textField.setMaxLength(DefaultValuePropertyFactory.MAX_LENGTH)

    def updateFunction(config: GDSConfiguration) = {
      config.copy(nullValue = DefaultValue(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object DefaultValuePropertyFactory {
  val MAX_LENGTH = 68

  val validator = new AbstractStringValidator("Value {0} must be less than 68 characters") {
    def isValidString(value: String) = value.nonEmpty && value.length <= MAX_LENGTH
  }
}

