package edu.gemini.aspen.gds.web.ui.keywords.model

import edu.gemini.aspen.gds.api.GDSConfiguration
import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import java.util.regex.Pattern

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the key of a FITS Keyword
 */
class FitsKeywordPropertyFactory extends PropertyItemWrapperFactory(classOf[FitsKeyword], classOf[TextField]) {
  override val width = 150

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.keyword.key)
    textField.addValidator(FitsKeywordPropertyFactory.validator(textField))
    textField.setCaption("FITS Keyword")
    textField.setImmediate(true)
    textField.setValidationVisible(true)
    textField.setRequired(true)
    textField.setMaxLength(8)
    textField.setInvalidAllowed(false)

    def updateFunction(config: GDSConfiguration) = {
      config.copy(keyword = new FitsKeyword(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object FitsKeywordPropertyFactory {
  def validator(textField: TextField) = new AbstractStringValidator("Value {0} must be a valid FITS Keyword, more than 0 and up to 8 characters") {
    def isValidString(value: String) = Pattern.compile(FitsKeyword.KEY_FORMAT).matcher(value.toUpperCase).matches

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
