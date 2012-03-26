package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.ui.NativeSelect
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.api.{FitsType, DataType, GDSConfiguration}

/**
 * PropertyItemWrapperFactory for DataType that uses a ComboBox to select a given data type
 */
class DataTypePropertyFactory extends PropertyItemWrapperFactory(classOf[DataType], classOf[NativeSelect]) {
  override val width = 100
  
  override def buildPropertyControlAndWrapper(config: GDSConfiguration) = {
    val select = new NativeSelect("", DataTypePropertyFactory.dataTypes)
    select.setNullSelectionAllowed(false)
    select.setCaption("Data Type")
    select.setRequired(true)
    select.select(config.dataType.name)

  def updateFunction(config: GDSConfiguration) = {
      Option(select.getValue) map {
        v => config.copy(dataType = DataType(v.toString))
      } getOrElse {
        config
      }
    }

    (select, updateFunction)
  }
}

object DataTypePropertyFactory {
  // The list is here to save memory
  val dataTypes = FitsType.TypeNames.values.toList map {
    _.toString
  }
}
