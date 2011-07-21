package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{ArrayIndex, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class ArrayIndexPropertyFactory extends PropertyItemWrapperFactory(classOf[ArrayIndex], classOf[TextField]) {
  override val width = 40

  override val title = "index"

  val validator = new AbstractStringValidator("Value {0} must be less than 8 characters") {
    def isValidString(value: String) = value.length <= 9
  }

  override def createItemAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField("", config.arrayIndex.value.toString)
    textField.addValidator(validator)
    textField.setCaption("Array Index")
    textField.setRequired(true)
    textField.setImmediate(true)
    textField.setRequired(true)
    textField.setMaxLength(3)
    textField.setWidth("30px")
    textField.setInvalidAllowed(false)

    def wrapper(config: GDSConfiguration) = {
      config.copy(arrayIndex = ArrayIndex(textField.getValue.toString.toInt))
    }

    (textField, wrapper)
  }
}