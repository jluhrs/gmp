package edu.gemini.aspen.gds.web.ui.status.model

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions._
import java.util.concurrent.TimeUnit._
import java.util.concurrent.atomic.AtomicInteger
import com.google.common.cache.CacheBuilder

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.web.ui.status.{ObservationError, Successful, PropertyValuesHelper}
import edu.gemini.aspen.gds.api.Conversions._
import org.apache.felix.ipojo.handlers.event.Subscriber
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.GDSStartObservation
import edu.gemini.aspen.gds.api.GDSObservationTimes
import edu.gemini.aspen.gds.api.GDSEndObservation
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.observationstate.{ObservationStateConsumer, ObservationStateProvider}
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.data.DataLabel
import fits.FitsKeyword
import org.joda.time.DateTime

/**
 * Interface for the OSGiService */
trait ObservationsSource {
  def observations: Iterable[ObservationBean]

  def pending: Iterable[DataLabel]

  def registerListener(f:() => Unit)
}

/**
 * A PaxAppender service that will get log events from pax-logging
 */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationsSource], classOf[ObservationStateConsumer]))
class InMemoryObservationsSource(@Requires statusDB: StatusDatabaseService, @Requires obsState: ObservationStateProvider, @Requires top: Top) extends ObservationsSource with ObservationStateConsumer {
  val propertySources = new PropertyValuesHelper(statusDB, obsState, top)
  var listener:Option[() => Unit] = None

  val MAXSIZE = 10000

  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  // We index with an artificial value to avoid collisions with timestamps
  val index = new AtomicInteger(0)

  val observationBeansMap: ConcurrentMap[java.lang.Integer, ObservationBean] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .maximumSize(MAXSIZE).build[java.lang.Integer, ObservationBean]().asMap()

  val pendingObservations: ConcurrentMap[DataLabel, java.lang.Boolean] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .maximumSize(MAXSIZE).build[DataLabel, java.lang.Boolean]().asMap()

  def observations = observationBeansMap.values.toList.sortBy {_.timeStamp0.getOrElse(new DateTime()).getMillis} reverse

  def pending =  pendingObservations.keys

  @Validate
  def initLogListener() {}

  def registerListener(f:() => Unit) {
    listener = Some(f)
  }


  def onObservationError(label: DataLabel, s: String) = {
    pendingObservations.remove(label, true)
    doAppend(new ObservationBean(ObservationError, Some(new DateTime()), label))
    listener foreach (_.apply())
  }


  def doAppend(observation: ObservationBean) {
    val i = index.incrementAndGet()
    observationBeansMap += java.lang.Integer.valueOf(i) -> observation
  }

  /**
   * Will be called when when OBS_PREP obs event arrives
   */
  def receiveStartObservation(label: DataLabel) {
    pendingObservations += label -> true

    listener foreach (_.apply())
  }

  /**
   * Will be called when OBS_WRITE_DSET_END obs event arrives, and/or? the FITS file has been updated
   */
  def receiveEndObservation(label: DataLabel, missingKeywords: Traversable[FitsKeyword], errorKeywords: Traversable[(FitsKeyword, CollectionError.CollectionError)]) {
    pendingObservations.remove(label, true)

    doAppend(new ObservationBean(Successful, Some(new DateTime()), label))

    listener foreach (_.apply())
  }

  /**
   * Will be called when an observation end in an error
   */
  def receiveObservationError(label: DataLabel, message: String) {
    pendingObservations.remove(label, true)

    doAppend(new ObservationBean(ObservationError, Some(new DateTime()), label))

    listener foreach (_.apply())
  }
}