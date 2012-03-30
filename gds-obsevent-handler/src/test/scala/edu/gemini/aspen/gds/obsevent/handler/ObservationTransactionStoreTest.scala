package edu.gemini.aspen.gds.obsevent.handler

import org.junit.Assert._
import edu.gemini.aspen.giapi.data.DataLabel
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import java.util.concurrent.TimeUnit

@RunWith(classOf[JUnitRunner])
class ObservationTransactionStoreTest extends FunSuite {
  val dataLabel = new DataLabel("GS-2011")

  test("add and check transaction") {
    val store = new ObservationTransactionsStore
    assertFalse(store.hasTransaction(dataLabel))

    store.startTransaction(dataLabel)
    assertTrue(store.hasTransaction(dataLabel))
  }

  test("self expiraton") {
    val store = new ObservationTransactionsStore {
      override val expirationMillis = 10
    }
    assertFalse(store.hasTransaction(dataLabel))

    store.startTransaction(dataLabel)
    TimeUnit.MILLISECONDS.sleep(100)

    assertFalse(store.hasTransaction(dataLabel))
  }
}