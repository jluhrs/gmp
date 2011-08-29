package edu.gemini.aspen.gds.api.configuration

import edu.gemini.aspen.gds.api.GDSConfiguration

/**
 * Type to group the 2 possible values of a non blank configuration file line: GDSConfiguration and Comment
 * This is done via an implicit parameter. See FitsType for another example of the same technique
 */
abstract class ConfigType[T] {
  def ConfigTypeToString(item: ConfigItem[T]): String
}

object ConfigType {

  implicit object ConfigurationType extends ConfigType[GDSConfiguration] {

    override def ConfigTypeToString(item: ConfigItem[GDSConfiguration]): String = {
      item.value.formatForConfigFile
    }
  }

  implicit object CommentType extends ConfigType[Comment] {

    override def ConfigTypeToString(item: ConfigItem[Comment]): String = {
      item.value.toString
    }
  }

}