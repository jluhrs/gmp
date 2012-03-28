package edu.gemini.aspen.gds.obsevent.handler

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent, ObservationEventHandler}
import org.apache.felix.ipojo.handlers.event.publisher.Publisher
import org.apache.felix.ipojo.handlers.event.{Subscriber, Publishes}

/**
 * Very simple object that passes observation events through the OSGi EventAdmin using the iPojo adapters */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationEventHandler]))
class ObservationEvent2EventAdmin(publisher0:Publisher = null) extends ObservationEventHandler {
  @Publishes(name="obsrelay", topics = "edu/gemini/aspen/gds/obsevent/handler", dataKey = "observationevent")
  val publisher:Publisher = publisher0

  def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
    require(publisher != null)
    publisher.sendData((event, dataLabel))
  }

  // TODO delete
  @Subscriber(name="obsend", topics="obs", dataType = "scala.Tuple2", dataKey = "tupla")
  def sub(event: (ObservationEvent, DataLabel)) {
    println("GOT " + event)
  }
}