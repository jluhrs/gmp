package edu.gemini.aspen.gds.observationstate.impl

import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Component}
import org.apache.felix.ipojo.handlers.event.Subscriber
import edu.gemini.aspen.gds.api._
import java.util.logging.Logger

/**
 * Intermediate class to convert GDS events into calls to ObservationStateRegistrar */
@Component
@Instantiate
class GDSEventsListener(@Requires registrar: ObservationStateRegistrar) {
  private[this] val LOG = Logger.getLogger(getClass.getName)

  @Subscriber(name = "gds2eventsregsitrar", topics = "edu/gemini/aspen/gds/gdsevent", dataKey = "gdsevent", dataType = "edu.gemini.aspen.gds.api.GDSNotification")
  def gdsEvent(event: GDSNotification) {
    event match {
      case s: GDSStartObservation => registrar.startObservation(s.dataLabel)
      case e: GDSEndObservation => registrar.endObservation(e.dataLabel, e.writeTime, e.keywords)
      case t: GDSObservationTimes => registrar.registerTimes(t.dataLabel, t.times)
      case e: GDSObservationError => registrar.registerError(e.dataLabel, e.msg)
      case x => LOG.severe("Arrived unknown event type: " + x)
    }
  }

}