package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import com.vaadin.data.validator.AbstractStringValidator
import edu.gemini.aspen.gds.api.{HeaderIndex, GDSConfiguration}
import scala.collection.JavaConversions._
import com.vaadin.ui.{Select, NativeSelect}

/**
 * PropertyItemWrapperFactory for HeaderIndex using a TextField to make possible to edit
 * the name of a FITS Keyword
 */
class HeaderIndexPropertyFactory extends PropertyItemWrapperFactory(classOf[HeaderIndex], classOf[NativeSelect]) {
  val validator = new AbstractStringValidator("Value {0} must be a number") {
    def isValidString(value: String) = value.matches("""[\d+]+""")
  }

  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val allowedHeaders = (0 to 10) map {_.toString}
    val select = new NativeSelect("", allowedHeaders)
    select.setNullSelectionAllowed(false)
    select.setInvalidAllowed(false)
    select.addStyleName("small-combobox")
    select.select(config.index.index.toString)
    // In reality validator is not necessary for a NativeSelect
    select.addValidator(validator)

    def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(select.getValue) map {
        v => config.copy(index = HeaderIndex(v.toString.toInt))
      } getOrElse {
        config
      }
    }

    (select, wrapper)
  }
}













