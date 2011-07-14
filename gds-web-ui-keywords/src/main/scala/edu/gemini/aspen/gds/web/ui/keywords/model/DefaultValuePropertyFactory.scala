package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import com.vaadin.ui.TextField
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{DefaultValue, GDSConfiguration}

class DefaultValueTextField(defaultValue: DefaultValue) extends TextField("", defaultValue.value) with Comparable[TextField] {
  def compareTo(that: TextField) = this.getValue.toString.compareTo(that.getValue.toString)
}

/**
 * PropertyItemWrapperFactory for FitsKeyword that uses a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class DefaultValuePropertyFactory extends PropertyItemWrapperFactory(classOf[DefaultValue], classOf[DefaultValueTextField]) {
  val validator = new AbstractStringValidator("Value {0} must be less than 8 characters") {
    // todo verify max allowed length
    def isValidString(value: String) = value.length <= 80
  }

  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
  //    val textField = new TextField("", config.nullValue.value) with Comparable[TextField] {
  //      def compareTo(that: TextField) = this.getValue.toString.compareTo(that.getValue.toString)
  //    }
    val textField = new DefaultValueTextField(config.nullValue)
    textField.addValidator(validator)
    textField.setImmediate(true)
    textField.setRequired(true)
    textField.setMaxLength(80)
    textField.setInvalidAllowed(false)

    def wrapper(config: GDSConfiguration) = {
      config.copy(nullValue = DefaultValue(textField.getValue.toString))
    }

    (textField, wrapper)
  }
}













