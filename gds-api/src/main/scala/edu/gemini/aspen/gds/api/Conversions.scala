package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel
import fits.FitsKeyword

/**
 * Place to put implicit conversions for Java classes.
 */
object Conversions {
    implicit val fitsKeywordToString = (fits: FitsKeyword) => fits.key

    implicit val stringToFitsKeyword = (s: String) => new FitsKeyword(s)

    implicit val dataLabelToString = (dataLabel: DataLabel) => dataLabel.getName

    implicit val stringToDataLabel = (s: String) => new DataLabel(s)

    implicit val stringToInstrument = (name: String) => new Instrument(name)

    implicit val stringToGDSEvent = (name: String) => new GDSEvent(name)

    implicit val intToHeaderIndex = (index: Int) => new HeaderIndex(index)

    implicit val stringToDataType = (name: String) => new DataType(name)

    implicit val boolToMandatory = (value: Boolean) => new Mandatory(value)

    implicit val stringToDefaultValue = (value: String) => new DefaultValue(value)

    implicit val stringToSubsystem = (name: String) => new Subsystem(KeywordSource.withName(name))

    implicit val stringToChannel = (name: String) => new Channel(name)

    implicit val intToArrayIndex = (value: Int) => new ArrayIndex(value)

    implicit val stringToFormat = (value: String) => if(value.isEmpty){
      Format(None)
    }else{
      Format(Some(value))
    }

    implicit val stringToFitsComment = (value: String) => new FitsComment(value)
}