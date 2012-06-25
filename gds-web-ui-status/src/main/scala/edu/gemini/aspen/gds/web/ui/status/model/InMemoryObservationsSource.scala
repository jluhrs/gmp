package edu.gemini.aspen.gds.web.ui.status.model

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions._
import java.util.concurrent.TimeUnit._
import java.util.concurrent.atomic.AtomicInteger
import com.google.common.cache.CacheBuilder

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.gds.web.ui.status.{Successful, PropertyValuesHelper}
import edu.gemini.aspen.gds.api.Conversions._
import org.apache.felix.ipojo.handlers.event.Subscriber
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.GDSStartObservation
import edu.gemini.aspen.gds.api.GDSObservationTimes
import edu.gemini.aspen.gds.api.GDSEndObservation
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.data.DataLabel
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
@Provides(specifications = Array[Class[_]](classOf[ObservationsSource]))
class InMemoryObservationsSource(@Requires statusDB: StatusDatabaseService, @Requires obsState: ObservationStateProvider, @Requires top: Top) extends ObservationsSource {
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

  def observations = observationBeansMap.values

  def pending =  pendingObservations.keys

  @Validate
  def initLogListener() {}

  @Subscriber(name = "gds2eventsregsitrar", topics = "edu/gemini/aspen/gds/gdsevent", dataKey = "gdsevent", dataType = "edu.gemini.aspen.gds.api.GDSNotification")
  def gdsEvent(event: GDSNotification) {

    event match {
      case s: GDSStartObservation => onStartObservation(s)
      case e: GDSEndObservation => onEndObservation(e)
      case t: GDSObservationTimes => //registrar.registerTimes(t.dataLabel, t.times)
      case e: GDSObservationError => //registrar.registerError(e.dataLabel, e.msg)
      case x => sys.error("Shouldn't happen")
    }
  }

  def registerListener(f:() => Unit) {
    listener = Some(f)
  }

  def onStartObservation(s: GDSStartObservation) {
    pendingObservations += s.dataLabel -> true

    listener foreach (_.apply())
  }

  private def onEndObservation(e: GDSEndObservation) = {
    /*observations = propertySources.getLastDataLabels(10) map {
      l => //if (propertySources.isInError(l)) {
        val result = propertySources.getStatus(l).get
        new ObservationBean(result, propertySources.getTimestamp(l), l)
      /*} else {
        new Entry(l, propertySources.getTimes(l))
      }*/
    } take (10) toList*/
    pendingObservations.remove(e.dataLabel, true)

    doAppend(new ObservationBean(Successful, Some(new DateTime()), e.dataLabel))

    listener foreach (_.apply())
  }


  def doAppend(observation: ObservationBean) {
    val i = index.incrementAndGet()
    observationBeansMap += java.lang.Integer.valueOf(i) -> observation
  }
}