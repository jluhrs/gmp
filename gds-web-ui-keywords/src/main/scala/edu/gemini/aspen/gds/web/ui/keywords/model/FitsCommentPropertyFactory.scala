package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{FitsComment, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class FitsCommentPropertyFactory extends PropertyItemWrapperFactory(classOf[FitsComment], classOf[TextField]) {
  override val title = "Comment"
 // override val width = 140

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.fitsComment.value.toString)
    textField.addValidator(DefaultValuePropertyFactory.validator)
    textField.setCaption("FITS Comment")
    textField.setRequired(true)
    textField.setImmediate(true)
    textField.setInvalidAllowed(false)
    textField.setMaxLength(68)
    //textField.setSizeUndefined()
    textField.setWidth("100%")

    def updateFunction(config: GDSConfiguration) = {
      config.copy(fitsComment = FitsComment(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object FitsCommentPropertyFactory {
  val MAX_LENGTH = 68

  val validator = new AbstractStringValidator("Value {0} must be less than 68 characters") {
    def isValidString(value: String) = value.nonEmpty && value.length <= MAX_LENGTH
  }
}