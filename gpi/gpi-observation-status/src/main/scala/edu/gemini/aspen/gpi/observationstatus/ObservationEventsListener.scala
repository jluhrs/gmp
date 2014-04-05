package edu.gemini.aspen.gpi.observationstatus

import org.apache.felix.ipojo.annotations.{Invalidate, Requires, Instantiate, Component}
import scala.collection.JavaConversions._
import org.apache.felix.ipojo.handlers.event.Subscriber
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter
import edu.gemini.aspen.giapi.status.impl.BasicStatus
import scala.collection.concurrent
import edu.gemini.aspen.giapi.data.DataLabel
import com.google.common.cache.{RemovalNotification, RemovalListener, CacheLoader, CacheBuilder}
import java.util.concurrent.TimeUnit._
import edu.gemini.aspen.gds.api.GDSStartObservation
import edu.gemini.aspen.gds.api.GDSObservationError
import edu.gemini.aspen.gds.api.GDSEndObservation
import com.google.common.base.Stopwatch
import java.util.{TimerTask, Timer}

/**
 * Intermediate class to convert GDS events into status items for GPI */
@Component
@Instantiate
class ObservationEventsListener(@Requires gmpTop: Top, @Requires statusSetter: IStatusSetter) {
  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  private val cache = CacheBuilder.newBuilder()
      .removalListener(new RemovalListener[DataLabel, ObsTimerTask] {
        override def onRemoval(removalNotification: RemovalNotification[DataLabel, ObsTimerTask]) {
          removalNotification.getValue.cancel()
        }
      })
      .expireAfterWrite(expirationMillis, MILLISECONDS)
      .build[DataLabel, ObsTimerTask](new CacheLoader[DataLabel, ObsTimerTask]() {
        override def load(label: DataLabel) = ObsTimerTask()
      })

  val timer = new Timer()

  case class ObsTimerTask extends TimerTask{
    override def run() = {
      println("TIMER")
    }
  }

  @Invalidate
  def invalidate() {
    cache.invalidateAll()
  }

  @Subscriber(name = "gpiobseventlistener", topics = "edu/gemini/aspen/gds/gdsevent", dataKey = "gdsevent", dataType = "edu.gemini.aspen.gds.api.GDSNotification")
  def gdsEvent(event: GDSNotification) {
    event match {
      case GDSStartObservation(dataLabel) =>
        val timerTask = cache.get(dataLabel)
        timer.scheduleAtFixedRate(timerTask, 0, 100)
        statusSetter.setStatusItem(new BasicStatus(gmpTop.buildStatusItemName("observationDataLabel"), dataLabel.getName))
      case e: GDSEndObservation           => println(e.dataLabel, e.writeTime, e.keywords)
      case e: GDSObservationError         => println(e.dataLabel, e.msg)
      case _                              => // Ignore
    }
  }

}
