package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{Format, GDSConfiguration}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationParser
import edu.gemini.aspen.giapi.web.ui.vaadin.components.TextField
import edu.gemini.aspen.giapi.web.ui.vaadin._
import edu.gemini.aspen.gds.api.Conversions.stringToFormat

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the key of a FITS Keyword
 */
class FormatPropertyFactory extends PropertyItemWrapperFactory(classOf[Format], classOf[TextField]) {
  override val width = 40
  override val title = "Format"

  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val textField = new TextField(caption = "Format",
      value = config.format.value.getOrElse(""),
      width = 30 px,
      required = true,
      immediate = false,
      maxLength = 3,
      invalidAllowed = false
    )
    textField.addValidator(FormatPropertyFactory.validator(textField))

    def updateFunction(config: GDSConfiguration) = {
      config.copy(format = stringToFormat(textField.getValue.toString))
    }

    (textField, updateFunction)
  }
}

object FormatPropertyFactory {
  def validator(textField: TextField) = new AbstractStringValidator("Format must be a standard Java Formatter syntax:  %[argument_index$][flags][width][.precision]conversion (ex.: '%20.13G'") {
    def isValidString(value: String) = GDSConfigurationParser.internalFormat.findFirstIn(value).isDefined

  }
}