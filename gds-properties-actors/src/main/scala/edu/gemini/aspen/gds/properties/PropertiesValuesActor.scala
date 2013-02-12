package edu.gemini.aspen.gds.properties

import edu.gemini.aspen.gds.api._

/**
 * Actor that can produce as a reply of a Collect request with the value of a java system property
 * to a single fitsKeyword
 */
class PropertiesValuesActor(configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    Option(System.getProperty(configuration.channel.name)).map {
      _ => valueToCollectedValue(System.getProperty(configuration.channel.name))
    }.toList
  }

}
