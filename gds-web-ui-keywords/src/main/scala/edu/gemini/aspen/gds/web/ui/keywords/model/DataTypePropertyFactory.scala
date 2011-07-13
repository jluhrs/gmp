package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.Item
import com.vaadin.ui.NativeSelect
import edu.gemini.aspen.giapi.data.ObservationEvent
import edu.gemini.aspen.gds.api.{FitsType, DataType, GDSEvent, GDSConfiguration}
import scala.collection.JavaConversions._

/**
 * PropertyItemWrapperFactory for DataType that uses a ComboBox to select a given data type
 */
class DataTypePropertyFactory extends PropertyItemWrapperFactory(classOf[DataType], classOf[NativeSelect]) {
  val dataTypes = List("STRING", "DOUBLE", "INT")

  override def createItemAndWrapper(config: GDSConfiguration, item: Item) = {
    val select = new NativeSelect("", dataTypes)
    select.setNullSelectionAllowed(false)
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













