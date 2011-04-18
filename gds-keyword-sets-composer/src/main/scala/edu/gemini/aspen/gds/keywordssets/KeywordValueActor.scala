package edu.gemini.aspen.gds.keywordssets

import actors.Actor

case class Collect()

/**
 * Trait for an actor that retrieve a specific value
 */
trait KeywordValueActor extends Actor {
    def act() {}
}

