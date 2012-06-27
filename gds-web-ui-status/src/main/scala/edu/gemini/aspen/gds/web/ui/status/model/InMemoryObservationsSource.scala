package edu.gemini.aspen.gds.web.ui.status.model

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions._
import java.util.concurrent.TimeUnit._
import java.util.concurrent.atomic.AtomicInteger
import com.google.common.cache.CacheBuilder

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.observationstate.{ObservationInfo, ObservationStateConsumer}
import edu.gemini.aspen.giapi.data.DataLabel

/**
 * Interface for the OSGiService */
trait ObservationsSource {
  def observations: Iterable[ObservationInfo]

  def pending: Iterable[DataLabel]

  def registerListener(f:() => Unit)
}

/**
 * A PaxAppender service that will get log events from pax-logging
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationsSource], classOf[ObservationStateConsumer]))
class InMemoryObservationsSource extends ObservationsSource with ObservationStateConsumer {
  var listener:Option[() => Unit] = None

  val MAXSIZE = 10000

  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  // We index with an artificial value to avoid collisions with timestamps
  val index = new AtomicInteger(0)

  val observationBeansMap: ConcurrentMap[java.lang.Integer, ObservationInfo] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .maximumSize(MAXSIZE).build[java.lang.Integer, ObservationInfo]().asMap()

  val pendingObservations: ConcurrentMap[DataLabel, java.lang.Boolean] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .maximumSize(MAXSIZE).build[DataLabel, java.lang.Boolean]().asMap()

  def observations = observationBeansMap.values.toList.sortBy {_.timeStamp.getMillis} reverse

  def pending =  pendingObservations.keys

  @Validate
  def initLogListener() {}

  def registerListener(f:() => Unit) {
    listener = Some(f)
  }

  def doAppend(observation: ObservationInfo) {
    val i = index.incrementAndGet()
    observationBeansMap += java.lang.Integer.valueOf(i) -> observation
  }

  /**
   * Will be called when when OBS_PREP obs event arrives
   */
  override def receiveStartObservation(label: DataLabel) {
    pendingObservations += label -> true

    listener foreach (_.apply())
  }

  /**
   * Will be called when OBS_WRITE_DSET_END obs event arrives, and/or? the FITS file has been updated
   */
  override def receiveEndObservation(observationInfo:ObservationInfo) {
    pendingObservations.remove(observationInfo.dataLabel, true)

    doAppend(observationInfo)

    listener foreach (_.apply())
  }

  /**
   * Will be called when an observation end in an error
   */
  override def receiveObservationError(observationInfo:ObservationInfo) {
    pendingObservations.remove(observationInfo.dataLabel, true)

    doAppend(observationInfo)

    listener foreach (_.apply())
  }
}