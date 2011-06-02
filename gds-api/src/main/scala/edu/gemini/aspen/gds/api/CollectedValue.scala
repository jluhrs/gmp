package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.HeaderItem

/**
 * Message indicating the resulting values
 */
case class CollectedValue[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(implicit val _type: FitsType[T])


/**
 * Companion object used to place implicit conversions
 */
object CollectedValue {
  implicit def collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit _type: FitsType[T]): HeaderItem = {
    _type.collectedValueToHeaderItem(collectedValue)
  }
}

