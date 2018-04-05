package edu.gemini.aspen.gds.observationstate.impl

import java.util.logging.Logger

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import org.osgi.service.event.{Event, EventHandler}

/**
 * Intermediate class to convert GDS events into calls to ObservationStateRegistrar */
class GDSEventsListener(registrar: ObservationStateRegistrar) extends EventHandler {
  private[this] val LOG = Logger.getLogger(getClass.getName)

  override def handleEvent(event: Event): Unit = {
    event.getProperty(GDSNotification.GDSNotificationKey) match {
      case s: GDSStartObservation => registrar.startObservation(s.dataLabel)
      case e: GDSEndObservation => registrar.endObservation(e.dataLabel, e.writeTime, e.keywords)
      case t: GDSObservationTimes => registrar.registerTimes(t.dataLabel, t.times)
      case e: GDSObservationError => registrar.registerError(e.dataLabel, e.msg)
      case x => LOG.severe("Arrived unknown event type: " + x)
    }
  }
}
