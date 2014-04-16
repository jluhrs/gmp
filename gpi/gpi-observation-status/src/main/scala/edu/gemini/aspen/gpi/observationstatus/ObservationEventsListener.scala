package edu.gemini.aspen.gpi.observationstatus

import org.apache.felix.ipojo.annotations.{Invalidate, Requires, Instantiate, Component}
import scala.collection.JavaConversions._
import org.apache.felix.ipojo.handlers.event.Subscriber
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter
import edu.gemini.aspen.giapi.status.impl.BasicStatus
import edu.gemini.aspen.giapi.data.DataLabel
import com.google.common.cache.{RemovalNotification, RemovalListener, CacheLoader, CacheBuilder}
import java.util.concurrent.TimeUnit._
import edu.gemini.aspen.gds.api.GDSStartObservation
import edu.gemini.aspen.gds.api.GDSObservationError
import edu.gemini.aspen.gds.api.GDSEndObservation
import java.util.{TimerTask, Timer}
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import scalaz._
import Scalaz._
import java.util.concurrent.{TimeUnit, Callable}
import com.google.common.base.Stopwatch
import java.util.logging.Logger

/**
 * Intermediate class to convert GDS events into status items for GPI */
@Component
@Instantiate
class ObservationEventsListener(@Requires gmpTop: Top, @Requires statusSetter: IStatusSetter, @Requires statusDB: StatusDatabaseService) {
  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  val LOG = Logger.getLogger(classOf[ObservationEventsListener].getName)

  val readoutOverhead = 10
  val writeOverhead = 7
  val perCoaddOverhead = 3

  private val cache = CacheBuilder.newBuilder()
      .removalListener(TimerRemoval)
      .expireAfterWrite(expirationMillis, MILLISECONDS)
      .build[DataLabel, ObsTimerTask](new CacheLoader[DataLabel, ObsTimerTask]() {
        override def load(label: DataLabel) = ObsTimerTask()
      })

  val timer = new Timer()

  @Invalidate
  def invalidate() {
    cache.invalidateAll()
  }

  @Subscriber(name = "gpiobseventlistener", topics = "edu/gemini/aspen/gds/gdsevent", dataKey = "gdsevent", dataType = "edu.gemini.aspen.gds.api.GDSNotification")
  def gdsEvent(event: GDSNotification) {
    event match {
      case GDSStartObservation(dataLabel) =>
        val coAdds = Option(statusDB.getStatusItem[Int](gmpTop.buildStatusItemName("currentNumCoadds"))).map(_.getValue)
        val exposureTime = Option(statusDB.getStatusItem[Float](gmpTop.buildStatusItemName("currentIntegrationTime"))).map(_.getValue)
        (exposureTime |@| coAdds)((e, c) => (e + perCoaddOverhead) * c + readoutOverhead + writeOverhead).foreach { observationTime =>
          LOG.info(s"Exposure started with exposure time ${exposureTime.get} and coadds ${coAdds.get}")
          val estimatedTimeLeft = 1000*observationTime/2
          LOG.info(s"Start counter to $estimatedTimeLeft")
          if (Option(cache.getIfPresent(dataLabel)).isEmpty) {
            val timerTask = cache.get(dataLabel, new Callable[ObsTimerTask] {
              override def call() = {
                ObsTimerTask(estimatedTimeLeft.toLong)
              }
            })

            val obsTimeItem = gmpTop.buildStatusItemName("ifs:estimatedObservationTime")
            LOG.info(s"Set estimated observation time $estimatedTimeLeft on $obsTimeItem")
            statusSetter.setStatusItem(new BasicStatus(obsTimeItem, estimatedTimeLeft.toInt))
            timer.scheduleAtFixedRate(timerTask, 0, 100)
          }
        }
        LOG.info(s"Set observationDataLabel to $dataLabel")
        statusSetter.setStatusItem(new BasicStatus(gmpTop.buildStatusItemName("ifs:observationDataLabel"), dataLabel.getName))
      case GDSEndObservation(dataLabel, _, _)           =>
        cache.get(dataLabel).endObservation()
        cache.invalidate(dataLabel)
      case GDSObservationError(dataLabel, _)         =>
        cache.get(dataLabel).endObservation()
        cache.invalidate(dataLabel)
      case _                              => // Ignore
    }
  }

  case class ObsTimerTask(observationTime: Double = 0) extends TimerTask{
    val stopwatch = Stopwatch.createStarted()
    override def run() = {
      //
      if (stopwatch.isRunning) {
        val remainingTime = observationTime - stopwatch.elapsed(TimeUnit.MILLISECONDS)
        statusSetter.setStatusItem(new BasicStatus(gmpTop.buildStatusItemName("ifs:timeLeft"), Math.max(0, remainingTime.toInt)))
        if (remainingTime <= 0 && stopwatch.isRunning) {
          cancel()
        }
      }
    }

    def endObservation() = {
      if (stopwatch.isRunning) {
        stopwatch.stop()
      }
      cancel()
      statusSetter.setStatusItem(new BasicStatus(gmpTop.buildStatusItemName("ifs:timeLeft"), 0))
    }

    def isRunning = stopwatch.isRunning
  }

  case object TimerRemoval extends RemovalListener[DataLabel, ObsTimerTask] {
    override def onRemoval(removalNotification: RemovalNotification[DataLabel, ObsTimerTask]) {
      if (removalNotification.getValue.isRunning) {
        removalNotification.getValue.endObservation()
      }
    }
  }

}
