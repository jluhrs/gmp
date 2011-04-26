package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.giapi.data.ObservationEvent._
import edu.gemini.aspen.giapi.data.{ObservationEvent, Dataset, ObservationEventHandler}
import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.gds.keywordssets.factory.StartAcquisitionActorsFactory
import edu.gemini.aspen.gds.keywordssets.{Init, KeywordSetComposer}

@Component
@Instantiate
@Provides(specifications = Array(classOf[ObservationEventHandler]))
class GDSObseventHandler(@Requires actorsFactory: StartAcquisitionActorsFactory) extends ObservationEventHandler {
    def onObservationEvent(event: ObservationEvent, dataset: Dataset) {
        event match {
            case OBS_START_ACQ => startAcquisition(dataset)
            case _ => 
        }
    }

    private def startAcquisition(dataset: Dataset) {
        new KeywordSetComposer(actorsFactory) ! Init(dataset)
    }
}