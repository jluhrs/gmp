package edu.gemini.aspen.gds.keywordssets

import edu.gemini.aspen.giapi.data.Dataset
import scala.actors.Actor

/**
 * Trait for objects that can provide a set of Actors
 * that can in turn retrieve keyword values
 */
trait KeywordActorsFactory {
    def startObservationActors(dataSet:Dataset): List[Actor]
}