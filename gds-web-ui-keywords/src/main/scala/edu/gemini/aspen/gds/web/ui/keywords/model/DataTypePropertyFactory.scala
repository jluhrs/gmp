package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.ui.NativeSelect
import edu.gemini.aspen.gds.api.{DataType, GDSConfiguration}
import scala.collection.JavaConversions._

/**
 * PropertyItemWrapperFactory for DataType that uses a ComboBox to select a given data type
 */
class DataTypePropertyFactory extends PropertyItemWrapperFactory(classOf[DataType], classOf[NativeSelect]) {
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val select = new NativeSelect("", DataTypePropertyFactory.dataTypes)
    select.setNullSelectionAllowed(false)
    select.setCaption("Data Type")
    select.setRequired(true)
    select.select(config.dataType.name)

  def wrapper(config: GDSConfiguration): GDSConfiguration = {
      Option(select.getValue) map {
        v => config.copy(dataType = DataType(v.toString))
      } getOrElse {
        config
      }
    }

    (select, wrapper)
  }
}

object DataTypePropertyFactory {
  val dataTypes = List("STRING", "DOUBLE", "INT")
}
