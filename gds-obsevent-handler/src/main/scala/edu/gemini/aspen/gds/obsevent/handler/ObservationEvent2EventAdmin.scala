package edu.gemini.aspen.gds.obsevent.handler

import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent, ObservationEventHandler}
import org.apache.felix.ipojo.handlers.event.publisher.Publisher
import org.apache.felix.ipojo.handlers.event.Publishes
import java.util.logging.Logger

/**
 * Very simple object that passes observation events through the OSGi EventAdmin using the iPojo adapters */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationEventHandler]))
class ObservationEvent2EventAdmin(publisher0:Publisher = null) extends ObservationEventHandler {
  private val LOG = Logger.getLogger(this.getClass.getName)

  @Publishes(name="obsrelay", topics = "edu/gemini/aspen/gds/obsevent/handler", dataKey = "observationevent")
  val publisher:Publisher = publisher0

  def onObservationEvent(event: ObservationEvent, dataLabel: DataLabel) {
    require(publisher != null)
    LOG.fine("Publishing event " + event + " " + dataLabel)
    publisher.sendData((event, dataLabel))
  }

}