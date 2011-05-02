package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.fits.{HeaderItem, DefaultHeaderItem}

/**
 * Message indicating the resulting values
 */
case class CollectedValue(keyword:FitsKeyword, value:AnyRef, comment:String, index:Int)

//object CollectedValue{
//  implicit def toHeaderItem(collectedValue:CollectedValue):HeaderItem = DefaultHeaderItem.create(collectedValue.keyword.getName,collectedValue.value.toString,collectedValue.comment)
////  implicit def fromHeaderItem(headerItem:HeaderItem):CollectedValue = new CollectedValue(new FitsKeyword(headerItem.getKeyword),headerItem.getValue,headerItem.getComment,0)
////  implicit def fromDefaultHeaderItem(headerItem:DefaultHeaderItem):CollectedValue = new CollectedValue(new FitsKeyword(headerItem.getKeyword),headerItem.getValue,headerItem.getComment,0)
//  //todo: check value conversion to string
//}

