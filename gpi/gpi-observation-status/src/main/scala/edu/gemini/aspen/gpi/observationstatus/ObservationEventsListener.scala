package edu.gemini.aspen.gpi.observationstatus

import org.apache.felix.ipojo.annotations.{Requires, Instantiate, Component}
import java.util.logging.Logger
import org.apache.felix.ipojo.handlers.event.Subscriber
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter
import edu.gemini.aspen.giapi.status.impl.BasicStatus

/**
 * Intermediate class to convert GDS events into status items for GPI */
@Component
@Instantiate
class ObservationEventsListener(@Requires gmpTop: Top, @Requires statusSetter: IStatusSetter) {
  private[this] val LOG = Logger.getLogger(getClass.getName)

  @Subscriber(name = "gpiobseventlistener", topics = "edu/gemini/aspen/gds/gdsevent", dataKey = "gdsevent", dataType = "edu.gemini.aspen.gds.api.GDSNotification")
  def gdsEvent(event: GDSNotification) {
    event match {
      case GDSStartObservation(dataLabel) => statusSetter.setStatusItem(new BasicStatus(gmpTop.buildStatusItemName("observationDataLabel"), dataLabel.getName))
      case e: GDSEndObservation   => println(e.dataLabel, e.writeTime, e.keywords)
      case e: GDSObservationError => println(e.dataLabel, e.msg)
      case _                      => // Ignore
    }
  }

}
