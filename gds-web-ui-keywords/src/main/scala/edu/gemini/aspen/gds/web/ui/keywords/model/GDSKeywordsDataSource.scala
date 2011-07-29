package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.util.IndexedContainer
import com.vaadin.data.Item
import edu.gemini.aspen.giapi.data.{FitsKeyword}
import edu.gemini.aspen.gds.api._
import scala.collection.JavaConversions._

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
abstract class GDSKeywordsDataSource(config: List[GDSConfiguration]) extends IndexedContainer {


  def addNewConfig(config: GDSConfiguration) {}

  // Contains the factories for each column
  val columnsDefinitions = Map[Class[_], PropertyItemWrapperFactory](
    classOf[Instrument] -> new InstrumentPropertyFactory,
    classOf[GDSEvent] -> new GDSEventPropertyFactory,
    classOf[FitsKeyword] -> new FitsKeywordPropertyFactory,
    classOf[HeaderIndex] -> new HeaderIndexPropertyFactory,
    classOf[DataType] -> new DataTypePropertyFactory,
    classOf[Mandatory] -> new MandatoryPropertyFactory,
    classOf[DefaultValue] -> new DefaultValuePropertyFactory,
    classOf[Subsystem] -> new SubsystemPropertyFactory,
    classOf[Channel] -> new ChannelPropertyFactory,
    classOf[ArrayIndex] -> new ArrayIndexPropertyFactory,
    classOf[FitsComment] -> new FitsCommentPropertyFactory
  )

  addContainerProperties

  protected[keywords] def addContainerProperties

  def propertyHeader(s: String):String

  /**
   * Returns a list of GDSConfiguration based of the originally passed but updated with the changes from the GUI
   */
  def toGDSConfiguration: List[GDSConfiguration] = config

  def propertyIds: List[String] = getContainerPropertyIds map {
    c: Any => c.toString
  } toList

  def propertyWidth(id: String): Int = columnsDefinitions find {
    case (k, m) => m.title == id
  } map {
    _._2.width
  } getOrElse (-1)
}

object GDSKeywordsDataSource {
  type WrappedConfigItem = (GDSConfiguration) => GDSConfiguration

  /**
   * Uses the wrapped functions to recursively get a new GDSConfiguration based on the actual values from the UI
   */
  protected[keywords] def itemToGDSConfiguration(config: GDSConfiguration, functions: List[WrappedConfigItem]): GDSConfiguration = {
    functions match {
      case List() => config
      case List(x) => x(config)
      case x :: rest => x(itemToGDSConfiguration(config, rest))
    }
  }

  /**
   * Returns a list of the fields of GDSConfiguration we are interested to display/edit
   */
  protected[keywords] def displayedFields =
    classOf[GDSConfiguration].getDeclaredFields.toList take (11)
}