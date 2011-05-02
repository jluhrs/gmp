package edu.gemini.aspen.gds.api

import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}
import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}

object Conversions {
  implicit def FitsKeywordToString(fits: FitsKeyword) = fits.getName

  implicit def StringToFitsKeyword(s: String) = new FitsKeyword(s)

  implicit def DataLabelToString(dataLabel: DataLabel) = dataLabel.getName

  implicit def StringToDataLabel(s: String) = new DataLabel(s)

  implicit def CollectedValueToHeaderItem(collectedValue: CollectedValue): HeaderItem = DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.toString, collectedValue.comment)

  implicit def HeaderItemToCollectedValue(headerItem: HeaderItem): CollectedValue = new CollectedValue(headerItem.getKeyword, headerItem.getValue, headerItem.getComment, 0)

  //todo: check value conversion to string
}