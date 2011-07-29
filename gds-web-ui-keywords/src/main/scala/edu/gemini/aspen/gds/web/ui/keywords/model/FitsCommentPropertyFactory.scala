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
  override val width = 120


  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.fitsComment.value.toString)
    textField.addValidator(FitsCommentPropertyFactory.validator)
    textField.setCaption("FITS Comment")
    textField.setRequired(true)
    textField.setImmediate(true)
    textField.setInvalidAllowed(false)
    textField.setMaxLength(80)

    def updateFunction(config: GDSConfiguration) = {
      config.copy(fitsComment = FitsComment(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object FitsCommentPropertyFactory {
  val validator = new AbstractStringValidator("Value {0} must be less than 8 characters") {
    // todo check the lenght
    def isValidString(value: String) = value.length <= 80
  }
}