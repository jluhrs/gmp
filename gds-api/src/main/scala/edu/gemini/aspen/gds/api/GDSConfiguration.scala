package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.ObservationEvent
import fits.FitsKeyword

case class Instrument(name: String)

case class GDSEvent(name: String) {
  require(ObservationEvent.valueOf(name) != null)
}

case class HeaderIndex(index: Int) {
  require(index >= 0)
}

case class DataType(name: String) {
  require(FitsType.TypeNames.values.contains(FitsType.TypeNames.withName(name)))
}

// A keyword marked as mandatory will be left empty and an error will be put in the log
// if the value is not found
case class Mandatory(mandatory: Boolean)

// This value will be used if not mandatory and the value is not found
case class DefaultValue(value: String)

case class Subsystem(name: KeywordSource.Value)

case class Channel(name: String) {
  require(name.nonEmpty)
}

case class ArrayIndex(value: Int) {
  require(value >= 0)
}

case class FitsComment(value: String)

case class Format(value: Option[String]) {
  def getAsString = {
    value.getOrElse("")
  }
}

/**
 * Encapsulates a configuration item of GDS */
case class GDSConfiguration(instrument: Instrument,
                            event: GDSEvent,
                            keyword: FitsKeyword,
                            index: HeaderIndex,
                            dataType: DataType,
                            mandatory: Mandatory,
                            nullValue: DefaultValue,
                            subsystem: Subsystem,
                            channel: Channel,
                            arrayIndex: ArrayIndex,
                            format: Format,
                            fitsComment: FitsComment) {
  def isMandatory = mandatory.mandatory

  private def addTabs(str: String, tabs: Int) = {
    var tabsStr = "\t"
    for (i <- 1 to (tabs - str.length() / 8 - 1)) {
      tabsStr += "\t"
    }
    str + tabsStr
  }

  def formatForConfigFile: String = {
    instrument.name + "\t" +
      addTabs(event.name, 3) +
      addTabs(keyword.key, 2) +
      addTabs(index.index.toString, 1) +
      addTabs(dataType.name, 1) +
      addTabs(if (mandatory.mandatory) "T" else "F", 1) +
      addTabs(nullValue.value, 2) +
      addTabs(subsystem.name.toString, 1) +
      addTabs(channel.name, 3) +
      addTabs(arrayIndex.value.toString, 1) +
      addTabs("\"" + format.value.getOrElse("") + "\"", 1) +
      "\"" + fitsComment.value + "\""
  }
}