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
    textField.addValidator(DefaultValuePropertyFactory.validator(textField))
    textField.setCaption("Default Value")
    textField.setImmediate(true)
    textField.setRequired(true)
    textField.setValidationVisible(true)
    textField.setInvalidAllowed(false)
    textField.setMaxLength(DefaultValuePropertyFactory.MAX_LENGTH)

    def updateFunction(config: GDSConfiguration) = {
      config.copy(nullValue = DefaultValue(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object DefaultValuePropertyFactory {
  val MAX_LENGTH = 68

  def validator(textField: TextField) = new AbstractStringValidator("Value {0} must be more than 0 and less than 68 characters") {
    def isValidString(value: String) = value.nonEmpty && value.length <= MAX_LENGTH

    override def validate(value: AnyRef) {
      if (!isValid(value)) {
        textField.addStyleName("validation-error")
      } else {
        textField.removeStyleName("validation-error")
      }
      super.validate(value)
    }
  }

}

