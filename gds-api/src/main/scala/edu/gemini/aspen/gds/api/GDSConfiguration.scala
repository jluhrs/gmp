package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword

case class Instrument(name: String)

case class GDSEvent(name: String)

case class HeaderIndex(index: Int)

case class DataType(name: String)

case class Mandatory(mandatory: Boolean)

case class NullValue(value: String)

case class Subsystem(name: String)

case class Channel(name: String)

case class ArrayIndex(value: String)

case class FitsComment(value: String)

/**
 * Encapsulates a configuration item of GDS
 */
case class GDSConfiguration(instrument: Instrument,
                            event: GDSEvent,
                            keyword: FitsKeyword,
                            index:HeaderIndex,
                            dataType: DataType,
                            mandatory: Mandatory,
                            nullValue: NullValue,
                            subsystem: Subsystem,
                            channel: Channel,
                            arrayIndex: ArrayIndex,
                            fitsComment:FitsComment) {
    def isMandatory = mandatory.mandatory
}