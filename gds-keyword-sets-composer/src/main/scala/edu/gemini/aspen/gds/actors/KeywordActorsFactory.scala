package edu.gemini.aspen.gds.actors

import edu.gemini.aspen.giapi.data.DataLabel
import actors.Actor
import edu.gemini.aspen.gds.keywordssets.configuration.GDSConfiguration

/**
 * Trait for objects that can provide a set of Actors
 * that can in turn retrieve keyword values
 */
trait KeywordActorsFactory {
    def startAcquisitionActors(dataLabel:DataLabel): List[Actor]

    def endAcquisitionActors(dataLabel:DataLabel): List[Actor]
}