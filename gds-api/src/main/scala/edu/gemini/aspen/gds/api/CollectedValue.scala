package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.FitsKeyword


/**
 * Message indicating the resulting values
 */
case class CollectedValue(keyword:FitsKeyword, value:AnyRef, comment:String)
