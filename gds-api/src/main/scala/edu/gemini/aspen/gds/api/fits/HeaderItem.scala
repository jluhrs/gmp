package edu.gemini.aspen.gds.api.fits

import edu.gemini.aspen.gds.api.FitsType

/**
 * Class encapsulating a single header item containing a keyword, value and comment */
case class HeaderItem[T](keywordName: FitsKeyword, value: T, comment: String, format: Option[String])(implicit val _type: FitsType[T])