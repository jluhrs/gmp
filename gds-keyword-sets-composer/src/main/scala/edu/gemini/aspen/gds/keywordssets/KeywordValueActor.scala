package edu.gemini.aspen.gds.keywordssets

import actors.Actor
import edu.gemini.aspen.giapi.data.FitsKeyword

/**
 * Message indicating that a value should be collected
 */
case class Collect()

/**
 * Message indicating the resulting values
 */
case class CollectedValue(keyword:FitsKeyword, value:AnyRef, comment:String)


/**
 * Trait for an actor that retrieve a specific value
 *
 * It is expected that the reply will be a List[CollectedValues]
 */
trait KeywordValueActor extends Actor;