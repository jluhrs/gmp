package edu.gemini.aspen.gds.web.ui.keywords.model

import com.vaadin.data.util.IndexedContainer
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api._
import configuration.ConfigItem
import scala.collection.JavaConversions._

/**
 * This class is the data source backing the Table that shows the keywords
 *
 * It turn it can read the modified values on the table and produce an edited list of GDSConfigurations
 */
abstract class GDSKeywordsDataSource(config: List[Option[ConfigItem[_]]]) extends IndexedContainer {
  //index of the next item if we want to add one
  var last: Int = config.size
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

  /**
   * Returns a list of GDSConfiguration based of the originally passed but updated with the changes from the GUI
   */
  protected[keywords] def toGDSConfiguration: List[Option[ConfigItem[_]]] = config

  /**
   * Method to make the Table#getContainerPropertyIds behave more like scala
   */
  protected[keywords] def propertyIds: List[String] = getContainerPropertyIds collect {
    case c: String => c
  } toList

  /**
   * Returns the width of a given column
   */
  protected[keywords] def propertyWidth(propertyId: String): Int = columnsDefinitions find {
    case (k, _) => k.getSimpleName == propertyId
  } map {
    _._2.width
  } getOrElse (-1)

  /**
   * Returns the header to use for a given column
   */
  protected[keywords] def propertyHeader(s: String): String

  /**
   * Adds the container properties, one per each column
   */
  protected[keywords] def addContainerProperties

  /**
   * Adds a new configuration item
   */
  protected[keywords] def addNewConfig(config: GDSConfiguration) {}
}

object GDSKeywordsDataSource {
  // Type of a function that can convert one config to another based on the contents of one GUI control
  type ConfigUpdateFunction = (GDSConfiguration) => GDSConfiguration

  /**
   * Uses the wrapped functions to recursively get a new GDSConfiguration based on the actual values from the UI
   */
  protected[keywords] def itemToGDSConfiguration(config: GDSConfiguration, functions: List[ConfigUpdateFunction]): GDSConfiguration = {
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