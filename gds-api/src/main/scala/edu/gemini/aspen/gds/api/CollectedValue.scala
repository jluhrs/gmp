package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}

/**
 * Message indicating the resulting values
 */
case class CollectedValue[T](keyword: FitsKeyword, value: T, comment: String, index: Int)(implicit val _type: FitsType[T])

case class MyHeaderItem[T](keyword: String, value:T, comment:String)(implicit _type: FitsType[T]) extends HeaderItem {
    def getComment = comment

    def getBooleanValue = value.asInstanceOf[Boolean]

    def getDoubleValue = value.asInstanceOf[Double]

    def getIntValue = value.asInstanceOf[Int]

    def getValue = value.toString

    def isStringValue = {
        _type match {
            case x if x==FitsType.StringType => true
            case _ => false
        }
    }

    def getKeyword = keyword
}

/**
 * Companion object used to place implicit conversions
 */
object CollectedValue {
  implicit def collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit _type: FitsType[T]): HeaderItem = {
//    _type match {
//      case FitsType.IntegerType => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[Int], collectedValue.comment)
//      case FitsType.DoubleType => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[Double], collectedValue.comment)
//      case FitsType.StringType => DefaultHeaderItem.create(collectedValue.keyword.getName, collectedValue.value.asInstanceOf[String], collectedValue.comment)
//    } //todo: another case cannot happen, but what if?
      //new MyHeaderItem(collectedValue.keyword.getName, collectedValue.value, collectedValue.comment)(_type)
      _type.collectedValueToHeaderItem(collectedValue)
  }
    //implicit def collectedValueToHeaderItem[T](collectedValue: CollectedValue[T])(implicit manifest:scala.reflect.Manifest[T]): HeaderItem = {

    //}


  //not sure if this is useful, as the HeatherItem doesn't have the Header index
  //implicit def headerItemToCollectedValue(headerItem: HeaderItem): CollectedValue = new CollectedValue(headerItem.getKeyword, headerItem.getValue, headerItem.getComment, 0)

}

//helper object to be able to extract implicit parameter from case class while pattern matching
object _CollectedValue {
  def unapply(in: CollectedValue[_]) = Some(in.keyword, in.value, in.comment, in.index, in._type)
}