package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword

case class Instrument(name: String)

case class GDSEvent(name: String)

case class HeaderIndex(index: Int)

case class DataType(name: String)

// A keyword marked as mandatory will be left empty and an error will be put in the log
// if the value is not found
case class Mandatory(mandatory: Boolean)

// This value will be used if not mandatory and the value is not found
case class DefaultValue(value: String)

case class Subsystem(name: KeywordSource.Value)

case class Channel(name: String)

case class ArrayIndex(value: Int)

case class FitsComment(value: String)

/**
 * Encapsulates a configuration item of GDS
 */
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
                            fitsComment: FitsComment) {
    def isMandatory = mandatory.mandatory
}