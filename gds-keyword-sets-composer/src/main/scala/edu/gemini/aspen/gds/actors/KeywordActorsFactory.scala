package edu.gemini.aspen.gds.actors

import edu.gemini.aspen.giapi.data.DataLabel
import actors.Actor
import edu.gemini.aspen.gds.keywordssets.configuration.GDSConfiguration

/**
 * Trait for objects that can provide a set of Actors
 * that can in turn retrieve keyword values
 */
trait KeywordActorsFactory {
    /**
     * Request the factory to create and start actors required for the start acquisition part
     */
    def startAcquisitionActors(dataLabel:DataLabel): List[Actor]

    /**
     * Request the factory to create and start actors required for the end acquisition part
     */
    def endAcquisitionActors(dataLabel:DataLabel): List[Actor]

    /**
     * Passes the global GDS configuration along
     */
    def configure(configuration:List[GDSConfiguration])
}