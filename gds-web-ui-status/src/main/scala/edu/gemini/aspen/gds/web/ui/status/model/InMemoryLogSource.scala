package edu.gemini.aspen.gds.web.ui.status.model

import scala.collection.mutable.ConcurrentMap
import scala.collection.JavaConversions._
import java.util.concurrent.TimeUnit._
import java.util.concurrent.atomic.AtomicInteger
import org.apache.felix.ipojo.annotations._
import com.google.common.cache.CacheBuilder
import edu.gemini.aspen.gds.web.ui.status.PropertyValuesHelper
import edu.gemini.aspen.gds.api.Conversions._

/**
 * A PaxAppender service that will get log events from pax-logging */
/*@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationsSource])*/
class InMemoryLogSource(propertySources: PropertyValuesHelper) extends ObservationsSource {
  def refresh() = {
    observations = propertySources.getLastDataLabels(10) map {
      l => //if (propertySources.isInError(l)) {
        new ObservationBean(propertySources.getTimestamp(l), l)
      /*} else {
        new Entry(l, propertySources.getTimes(l))
      }*/
    } take (10) toList

    println(observations)
    observations
  }

  val MAXSIZE = 10000
  var observations = List[ObservationBean]()
  refresh()

  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  // We index with an artificial value to avoid collisions with timestamps
  val index = new AtomicInteger(0)
  /*val logEventsMap: ConcurrentMap[java.lang.Integer, ObservationBean] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .maximumSize(MAXSIZE)
    .build[java.lang.Integer, ObservationBean]().asMap*/

  @Validate
  def initLogListener() {}

  def doAppend(observation: ObservationBean) {
    //val i = index.incrementAndGet()
    //logEventsMap += java.lang.Integer.valueOf(i) -> observation
  }


}