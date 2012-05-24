package edu.gemini.aspen.gds.health

import org.junit.Assert._
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider
import edu.gemini.aspen.giapi.statusservice.{StatusHandlerAggregateImpl, StatusService}
import org.mockito.Mockito._
import org.mockito.Matchers._
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusHandler}
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandlerImpl
import java.util.concurrent.{CountDownLatch, TimeUnit}
import actors.threadpool.AtomicInteger
import edu.gemini.aspen.gmp.top.Top
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class GdsHealthTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  val healthName = "gpitest:gds:health"
  val testCounter = new AtomicInteger(0)

  val agg = new StatusHandlerAggregateImpl
  val provider = new ActiveMQJmsProvider("vm://GdsHealthTest?broker.useJmx=false&broker.persistent=false")
  val top = mock[Top]

  // Remove non actor based sources and add 2 for GDSObseventHandlerImpl and HeaderReceiver
  val expectedUpdates = (KeywordSource.values - KeywordSource.NONE - KeywordSource.INSTRUMENT).size + 2

  var statusservice: StatusService = _

  private class TestHandler(retries: Int) extends StatusHandler {
    override def getName = "Test Handler for GdsHealthTest"

    val counter = new AtomicInteger(0)
    val latch = new CountDownLatch(retries)
    var lastStatusItem: StatusItem[_] = _

    override def update[T](item: StatusItem[T]) {
      lastStatusItem = item
      counter.incrementAndGet()
      latch.countDown()
    }

    def waitForCompletion() {
      assertTrue(latch.await(20, TimeUnit.SECONDS))
    }

  }

  before {
    provider.startConnection()
    statusservice = new StatusService(agg, "Status Service " + testCounter.incrementAndGet(), ">")
    statusservice.startJms(provider)

    when(top.buildStatusItemName(anyString)).thenReturn(healthName)
  }

  after {
    statusservice.stopJms()
  }

  test("Bad Health") {
    val gdsHealth = new GdsHealth(top)
    gdsHealth.validate()
    gdsHealth.startJms(provider)

    val handler = new TestHandler(1)
    agg.bindStatusHandler(handler)

    handler.waitForCompletion()
    assertEquals(1, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.BAD)
    agg.unbindStatusHandler(handler)

    gdsHealth.stopJms()
  }

  test("Warning Health") {
    val gdsHealth = new GdsHealth(top)
    gdsHealth.validate()
    gdsHealth.startJms(provider)

    val handler = new TestHandler(2)
    agg.bindStatusHandler(handler)

    gdsHealth.bindGDSObseventHandler(mock[GDSObseventHandlerImpl])
    handler.waitForCompletion()
    assertEquals(2, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.WARNING)
    agg.unbindStatusHandler(handler)

    gdsHealth.stopJms()
  }

  def bindAllHealthSources(gdsHealth:GdsHealth) {
    gdsHealth.bindGDSObseventHandler(mock[GDSObseventHandlerImpl])
    val fact = mock[KeywordActorsFactory]
    for (source <- (KeywordSource.values - KeywordSource.NONE - KeywordSource.INSTRUMENT)) {
      println("Bind source " + source)
      when(fact.getSource).thenReturn(source)
      gdsHealth.bindActorFactory(fact)
    }

    gdsHealth.bindHeaderReceiver()
  }

  test("Good Health") {
    val gdsHealth = new GdsHealth(top)
    gdsHealth.validate()
    gdsHealth.startJms(provider)

    val handler = new TestHandler(expectedUpdates + 1)
    agg.bindStatusHandler(handler)

    bindAllHealthSources(gdsHealth)
    handler.waitForCompletion()
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.GOOD)
    agg.unbindStatusHandler(handler)

    gdsHealth.stopJms()
  }

  test("Unbind some elements to warning") {
    val gdsHealth = new GdsHealth(top)
    gdsHealth.validate()
    gdsHealth.startJms(provider)

    val startHandler = new TestHandler(expectedUpdates - 1)
    agg.bindStatusHandler(startHandler)
    bindAllHealthSources(gdsHealth)
    startHandler.waitForCompletion()
    agg.unbindStatusHandler(startHandler)

    val handler = new TestHandler(2)
    agg.bindStatusHandler(handler)

    gdsHealth.unbindHeaderReceiver()
    handler.waitForCompletion()
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.WARNING)
    agg.unbindStatusHandler(handler)

    gdsHealth.stopJms()

  }

  test("Unbind some elements to bad") {
    val gdsHealth = new GdsHealth(top)
    gdsHealth.validate()
    gdsHealth.startJms(provider)

    val startHandler = new TestHandler(expectedUpdates)
    agg.bindStatusHandler(startHandler)
    bindAllHealthSources(gdsHealth)
    startHandler.waitForCompletion()
    agg.unbindStatusHandler(startHandler)

    val warningHandler = new TestHandler(2)
    agg.bindStatusHandler(warningHandler)

    gdsHealth.unbindHeaderReceiver()
    warningHandler.waitForCompletion()
    agg.unbindStatusHandler(warningHandler)

    val handler = new TestHandler(1)
    agg.bindStatusHandler(handler)

    gdsHealth.unbindGDSObseventHandler(mock[GDSObseventHandlerImpl])
    handler.waitForCompletion()
    assertEquals(1, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.BAD)
    agg.unbindStatusHandler(handler)

    gdsHealth.stopJms()
  }

}