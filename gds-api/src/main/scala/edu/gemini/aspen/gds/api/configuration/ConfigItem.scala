package edu.gemini.aspen.gds.api.configuration


/**
 * This class encapsulates a line in a config file
 */
class ConfigItem[T](val value: T)(implicit val _type: ConfigType[T]) {
  override def toString: String = "ConfigItem(" + value.toString() + ")"

  override def equals(other: Any): Boolean = other match {
    case that: ConfigItem[_] => (that canEqual this) && value == that.value
    case _ => false
  }

  // Used by equals and can be overriden by extensions
  def canEqual(other: Any): Boolean = other.isInstanceOf[ConfigItem[_]]

  override def hashCode: Int = 41 * (41 + value.##)
}
