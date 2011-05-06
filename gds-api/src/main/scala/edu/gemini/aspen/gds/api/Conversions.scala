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
}