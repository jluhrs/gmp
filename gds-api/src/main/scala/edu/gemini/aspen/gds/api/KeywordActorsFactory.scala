package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel

/**
 * Trait for objects that can provide a set of Actors
 * that can in turn retrieve keyword values
 */
trait KeywordActorsFactory {
    /**
     * Request the factory to create and start actors when the 
     */
    def buildInitializationActors(programID:String, dataLabel:DataLabel): List[KeywordValueActor]

  /**
   * Request the factory to create and start actors required for the prepare observation part
   */
    def buildPrepareObservationActors(dataLabel:DataLabel): List[KeywordValueActor] = List()

    /**
     * Request the factory to create and start actors required for the start acquisition part
     */
    def buildStartAcquisitionActors(dataLabel:DataLabel): List[KeywordValueActor]

    /**
     * Request the factory to create and start actors required for the end acquisition part
     */
    def buildEndAcquisitionActors(dataLabel:DataLabel): List[KeywordValueActor]

    /**
     * Passes the global GDS configuration along
     */
    def configure(configuration:List[GDSConfiguration])
}