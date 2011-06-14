package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}


/**
 * Place to put implicit conversions for Java classes.
 */
object Conversions {
  implicit def fitsKeywordToString(fits: FitsKeyword) = fits.getName

  implicit def stringToFitsKeyword(s: String) = new FitsKeyword(s)

  implicit def dataLabelToString(dataLabel: DataLabel) = dataLabel.getName

  implicit def stringToDataLabel(s: String) = new DataLabel(s)

  implicit def stringToInstrument(name: String) = new Instrument(name)

  implicit def stringToGDSEvent(name: String) = new GDSEvent(name)

  implicit def intToHeaderIndex(index: Int) = new HeaderIndex(index)

  implicit def stringToDataType(name: String) = new DataType(name)

  implicit def boolToMandatory(value: Boolean) = new Mandatory(value)

  implicit def stringToNullValue(value: String) = new DefaultValue(value)

  implicit def stringToSubsystem(name: String) = new Subsystem(name)

  implicit def stringToChannel(name: String) = new Channel(name)

  implicit def stringToArrayIndex(value: String) = new ArrayIndex(value)

  implicit def stringToFitsComment(value: String) = new FitsComment(value)
}