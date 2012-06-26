package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel
import fits.FitsKeyword

/**
 * Place to put implicit conversions for Java classes.
 */
object Conversions {
    implicit def fitsKeywordToString(fits: FitsKeyword) = fits.key

    implicit def stringToFitsKeyword(s: String) = new FitsKeyword(s)

    implicit def dataLabelToString(dataLabel: DataLabel) = dataLabel.getName

    implicit def stringToDataLabel(s: String) = new DataLabel(s)

    implicit def stringToInstrument(name: String) = new Instrument(name)

    implicit def stringToGDSEvent(name: String) = new GDSEvent(name)

    implicit def intToHeaderIndex(index: Int) = new HeaderIndex(index)

    implicit def stringToDataType(name: String) = new DataType(name)

    implicit def boolToMandatory(value: Boolean) = new Mandatory(value)

    implicit def stringToDefaultValue(value: String) = new DefaultValue(value)

    implicit def stringToSubsystem(name: String) = new Subsystem(KeywordSource.withName(name))

    implicit def stringToChannel(name: String) = new Channel(name)

    implicit def intToArrayIndex(value: Int) = new ArrayIndex(value)

    implicit def stringToFormat(value: String) = if(value.isEmpty){
      new Format(None)
    }else{
      new Format(Some(value))
    }

    implicit def stringToFitsComment(value: String) = new FitsComment(value)
}