package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel, ObservationEventHandler}
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.keywordssets.factory.StartAcquisitionActorsFactory
import edu.gemini.aspen.gds.keywordssets.{Init, KeywordSetComposer}

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: StartAcquisitionActorsFactory) extends ObservationEventHandler {
    def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
        event match {
            case OBS_START_ACQ => startAcquisition(dataLabel)
            case _ =>
        }
    }

    private def startAcquisition(dataLabel: DataLabel) {
        new KeywordSetComposer(actorsFactory) ! Init(dataLabel)
    }
}