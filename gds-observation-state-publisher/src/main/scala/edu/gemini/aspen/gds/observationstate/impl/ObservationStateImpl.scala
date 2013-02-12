package edu.gemini.aspen.gds.observationstate.impl

import org.apache.felix.ipojo.annotations.{Requires, Provides, Instantiate, Component}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.observationstate._
import java.util.concurrent.TimeUnit._
import scala.collection.JavaConversions._
import edu.gemini.aspen.gds.api.{CollectedValue, CollectionError}
import collection.mutable.{SynchronizedSet, HashSet, Set, ConcurrentMap}
import java.util.Date
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import com.google.common.cache.CacheBuilder
import edu.gemini.aspen.gds.observationstate.ObservationInfo
import org.joda.time.{DateTime, Duration}

@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ObservationStateRegistrar], classOf[ObservationStateProvider]))
class ObservationStateImpl(@Requires obsStatePubl: ObservationStatePublisher) extends ObservationStateRegistrar with ObservationStateProvider {
  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  class ObservationState {
    val missingKeywords: Set[FitsKeyword] = new HashSet[FitsKeyword] with SynchronizedSet[FitsKeyword]
    val errorKeywords: Set[(FitsKeyword, CollectionError.CollectionError)] = new HashSet[(FitsKeyword, CollectionError.CollectionError)] with SynchronizedSet[(FitsKeyword, CollectionError.CollectionError)]
    val times: Set[(AnyRef, Option[Duration])] = new HashSet[(AnyRef, Option[Duration])] with SynchronizedSet[(AnyRef, Option[Duration])]
    var started = false
    var ended = false
    var inError = false
    var failed = false
    var timestamp = new Date()
  }

  val obsInfoMap: ConcurrentMap[DataLabel, ObservationState] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .build[DataLabel, ObservationState]().asMap()

  override def registerMissingKeyword(label: DataLabel, keywords: Traversable[FitsKeyword]) {
    obsInfoMap.getOrElseUpdate(label, new ObservationState).missingKeywords ++= keywords
    if (!keywords.isEmpty) {
      obsInfoMap.getOrElseUpdate(label, new ObservationState).inError = true
    }
  }

  //todo: use cause for something
  override def registerError(label: DataLabel, cause: String) {
    obsInfoMap.getOrElseUpdate(label, new ObservationState).failed = true
    obsStatePubl.publishObservationError(new ObservationInfo(label, ObservationError, errorMsg = Option(cause)))
  }

  override def registerCollectionError(label: DataLabel, errors: Traversable[(FitsKeyword, CollectionError.CollectionError)]) {
    obsInfoMap.getOrElseUpdate(label, new ObservationState).errorKeywords ++= errors
    if (!errors.isEmpty) {
      obsInfoMap.getOrElseUpdate(label, new ObservationState).inError = true
    }
  }

  override def registerTimes(label: DataLabel, times: Traversable[(AnyRef, Option[Duration])]) {
    obsInfoMap.getOrElseUpdate(label, new ObservationState).times ++= times
  }

  override def endObservation(label: DataLabel, writeTime:Long, collectedValues: Traversable[CollectedValue[_]]) {
    obsInfoMap.getOrElseUpdate(label, new ObservationState).ended = true
    obsStatePubl.publishEndObservation(new ObservationInfo(label, Successful, writeTime = Some(writeTime), collectedValues = collectedValues))
  }

  override def startObservation(label: DataLabel) {
    obsInfoMap.getOrElseUpdate(label, new ObservationState).started = true
    obsStatePubl.publishStartObservation(label)
  }

  //-----------------------------------------------------------------------

  override def isInError(label: DataLabel): Option[Boolean] = {
    obsInfoMap.get(label) map {_.inError}
  }

  override def isFailed(label: DataLabel): Option[Boolean] = {
    obsInfoMap.get(label) map {_.failed}
  }

  override def getLastDataLabel(n: Int): Traversable[DataLabel] = {
    obsInfoMap.toList.sortWith({
      (a: (DataLabel, ObservationState), b: (DataLabel, ObservationState)) => (a._2.timestamp.compareTo(b._2.timestamp) >= 0)
    }).take(n) map {
      a: (DataLabel, ObservationState) => a._1
    }
  }

  override def getTimes(label: DataLabel): Traversable[(AnyRef, Option[Duration])] = {
    obsInfoMap.getOrElse(label, new ObservationState).times
  }

  override def getTimestamp(label: DataLabel): Option[DateTime] = obsInfoMap.get(label) map { o => new DateTime(o.timestamp) }

  override def getMissingKeywords(label: DataLabel): Traversable[FitsKeyword] = {
    obsInfoMap.getOrElse(label, new ObservationState).missingKeywords
  }

  override def getKeywordsInError(label: DataLabel): Traversable[(FitsKeyword, CollectionError.CollectionError)] = {
    obsInfoMap.getOrElse(label, new ObservationState).errorKeywords
  }

  override def getObservationsInProgress: Traversable[DataLabel] = {
    obsInfoMap filter {
      case (key, value) => (value.started && !value.ended)
    } keySet
  }

  override def getLastDataLabel: Option[DataLabel] = {
    getLastDataLabel(1).headOption
  }
}