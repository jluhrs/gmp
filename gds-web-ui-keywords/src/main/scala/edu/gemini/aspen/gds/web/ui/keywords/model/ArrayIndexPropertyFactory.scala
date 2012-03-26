package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{ArrayIndex, GDSConfiguration}
import edu.gemini.aspen.giapi.web.ui.vaadin.components.TextField
import edu.gemini.aspen.giapi.web.ui.vaadin._

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the key of a FITS Keyword
 */
class ArrayIndexPropertyFactory extends PropertyItemWrapperFactory(classOf[ArrayIndex], classOf[TextField]) {
  override val width = 40
  override val title = "Index"

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField(caption = "Array Index",
      value = config.arrayIndex.value.toString,
      width = 30 px,
      required = true,
      immediate = false,
      maxLength = 3,
      invalidAllowed = false
    )
    textField.addValidator(ArrayIndexPropertyFactory.validator)

    def updateFunction(config: GDSConfiguration) = {
      config.copy(arrayIndex = ArrayIndex(textField.getValue.toString.toInt))
    }

    (textField, updateFunction)
  }
}

object ArrayIndexPropertyFactory {
  private val INDEX_MATCHER = """\d+""".r
  val validator = new AbstractStringValidator("Value {0} must be a positive integer") {
    def isValidString(value: String) = INDEX_MATCHER.findFirstIn(value).isDefined
  }
}