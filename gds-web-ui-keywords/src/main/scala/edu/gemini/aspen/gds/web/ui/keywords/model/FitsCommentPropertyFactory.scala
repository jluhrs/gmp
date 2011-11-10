package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{FitsComment, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class FitsCommentPropertyFactory extends PropertyItemWrapperFactory(classOf[FitsComment], classOf[TextField]) {
  override val title = "Comment"

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.fitsComment.value.toString)
    textField.addValidator(FitsCommentPropertyFactory.validator(textField))
    textField.setCaption("FITS Comment")
    textField.setRequired(false)
    textField.setImmediate(true)
    textField.setInvalidAllowed(false)
    textField.setMaxLength(68)
    textField.setWidth("100%")

    def updateFunction(config: GDSConfiguration) = {
      config.copy(fitsComment = FitsComment(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object FitsCommentPropertyFactory {
  val MAX_LENGTH = 68

  def validator(textField: TextField) = new AbstractStringValidator("Value {0} must be less than 68 characters and cannot be empty, enter NONE if not used") {
    def isValidString(value: String) = value.size < MAX_LENGTH

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