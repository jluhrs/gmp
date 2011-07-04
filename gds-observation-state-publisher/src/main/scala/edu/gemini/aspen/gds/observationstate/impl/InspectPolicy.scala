package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.{CollectedValue, ErrorPolicy}
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher

@Component
@Instantiate
@Provides(specifications = Array(classOf[ErrorPolicy]))
class InspectPolicy(@Requires obsStatePub: ObservationStatePublisher) extends ErrorPolicy {
    override val priority = 0

    override def applyPolicy(dataLabel: DataLabel, headers: Option[List[CollectedValue[_]]]): Option[List[CollectedValue[_]]] = headers

    //todo: update ObservationStatePublisher
}