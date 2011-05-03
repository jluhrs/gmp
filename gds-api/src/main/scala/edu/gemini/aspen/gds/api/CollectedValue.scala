package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}

/**
 * Message indicating the resulting values
 */
case class CollectedValue(keyword:FitsKeyword, value:AnyRef, comment:String, index:Int)

/**
 * Companion object used to place implicit conversions
 */
object CollectedValue{
  implicit def collectedValueToHeaderItem(collectedValue: CollectedValue): HeaderItem = DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.toString, collectedValue.comment)

  //not sure if this is useful, as the HeatherItem doesn't have the Header index
  //implicit def headerItemToCollectedValue(headerItem: HeaderItem): CollectedValue = new CollectedValue(headerItem.getKeyword, headerItem.getValue, headerItem.getComment, 0)

  //todo: check value conversion to string
}

