package edu.gemini.aspen.gds.api

import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}
import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}

object Conversions {
  implicit def fitsKeywordToString(fits: FitsKeyword) = fits.getName

  implicit def stringToFitsKeyword(s: String) = new FitsKeyword(s)

  implicit def dataLabelToString(dataLabel: DataLabel) = dataLabel.getName

  implicit def stringToDataLabel(s: String) = new DataLabel(s)

  implicit def collectedValueToHeaderItem(collectedValue: CollectedValue): HeaderItem = DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.toString, collectedValue.comment)

  implicit def headerItemToCollectedValue(headerItem: HeaderItem): CollectedValue = new CollectedValue(headerItem.getKeyword, headerItem.getValue, headerItem.getComment, 0)

  //todo: check value conversion to string
}