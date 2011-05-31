package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}

/**
 * Message indicating the resulting values
 */
case class CollectedValue[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(implicit val _value: FitsType[T])

/**
 * Companion object used to place implicit conversions
 */
object CollectedValue {
  implicit def collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit _value: FitsType[T]): HeaderItem = {
    _value.getType match {
      case i if i.isAssignableFrom(classOf[Int]) => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[Int], collectedValue.comment)
      case i if i.isAssignableFrom(classOf[Double]) => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[Double], collectedValue.comment)
      case i if i.isAssignableFrom(classOf[String]) => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[String], collectedValue.comment)
    } //todo: another case cannot happen, but what if?
  }


  //not sure if this is useful, as the HeatherItem doesn't have the Header index
  //implicit def headerItemToCollectedValue(headerItem: HeaderItem): CollectedValue = new CollectedValue(headerItem.getKeyword, headerItem.getValue, headerItem.getComment, 0)

}

//helper object to be able to extract implicit parameter from case class while pattern matching
object _CollectedValue {
  def unapply(in: CollectedValue[_]) = Some(in.keyword, in.value, in.comment, in.index, in._value)
}