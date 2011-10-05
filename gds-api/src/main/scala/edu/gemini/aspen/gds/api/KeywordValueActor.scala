package edu.gemini.aspen.gds.api

import actors.Actor
import scala.collection._

/**
 * Message indicating that a value should be collected
 */
case object Collect

/**
 * Trait for an actor that retrieve a specific value
 *
 * It is expected that the reply will be a List[CollectedValues]
 */
trait KeywordValueActor extends Actor {
  start()

  override def act() {
    react {
      case Collect => reply(collectValues())
    }
  }

  def collectValues(): immutable.List[CollectedValue[_]]
}