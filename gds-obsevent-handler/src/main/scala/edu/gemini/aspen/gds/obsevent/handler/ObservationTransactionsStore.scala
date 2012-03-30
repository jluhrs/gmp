package edu.gemini.aspen.gds.obsevent.handler

import scala.collection.JavaConversions._
import collection.mutable.ConcurrentMap
import com.google.common.cache.CacheBuilder
import edu.gemini.aspen.giapi.data.DataLabel
import java.util.concurrent.TimeUnit

/**
 * Simple class to store in a self-cleaning cache the observation transactions */
class ObservationTransactionsStore {
  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  val observationTransactions: ConcurrentMap[DataLabel, String] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, TimeUnit.MILLISECONDS)
    .build[DataLabel, String]().asMap()

  def startTransaction(dataLabel: DataLabel) {
    observationTransactions.put(dataLabel, "")
  }

  def hasTransaction(dataLabel: DataLabel): Boolean = observationTransactions.contains(dataLabel)
}