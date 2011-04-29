package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel, ObservationEventHandler}
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.keywordssets.{StartAcquisition, KeywordSetComposer}
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase
import edu.gemini.aspen.gds.keywordssets.factory.CompositeActorsFactory

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: CompositeActorsFactory, @Requires keywordsDatabase: KeywordsDatabase) extends ObservationEventHandler {
    def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
        event match {
            case OBS_START_ACQ => startAcquisition(dataLabel)
            case _ =>
        }
    }

    private def startAcquisition(dataLabel: DataLabel) {
        new KeywordSetComposer(actorsFactory, keywordsDatabase) ! StartAcquisition(dataLabel)
    }
}