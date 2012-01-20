package edu.gemini.aspen.gds.health

import org.junit.Assert._
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider
import edu.gemini.aspen.giapi.statusservice.{StatusHandlerAggregate, StatusHandlerAggregateImpl, StatusService}
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import org.mockito.Mockito._
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusHandler}
import org.junit.{After, Before, Test}
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler
import edu.gemini.jms.api.JmsProvider
import java.util.concurrent.{CountDownLatch, TimeUnit}
import actors.threadpool.AtomicInteger

class GdsHealthTest {
  val healthName = "gpi:gds:health"
  val testCounter = new AtomicInteger(0)

  var gdsHealth: GdsHealth = _
  val agg = new StatusHandlerAggregateImpl

  var statusservice: StatusService = _

  private class TestHandler(retries:Int) extends StatusHandler {
    override def getName = "Test Handler for GdsHealthTest"
    val counter = new AtomicInteger(0)
    val latch =  new CountDownLatch(retries)
    var lastStatusItem: StatusItem[_] = _

    override def update[T](item: StatusItem[T]) {
      lastStatusItem = item
      counter.incrementAndGet()
      latch.countDown()
    }

    def waitForCompletion() {
      latch.await(10, TimeUnit.SECONDS)
    }

  }
  @Before
  def init() {
    val provider: JmsProvider = new ActiveMQJmsProvider("vm://GdsHealthTest?broker.useJmx=false")
    statusservice = new StatusService(agg, provider, "Status Service " + testCounter.incrementAndGet(), ">")
    statusservice.initialize()
    TimeUnit.MILLISECONDS.sleep(1000)
    gdsHealth = new GdsHealth(provider)
  }

  @After
  def shutdown() {
    gdsHealth.invalidate()
    statusservice.stopComponent()
  }

  @Test
  def testBad() {
    val handler = new TestHandler(1)
    agg.bindStatusHandler(handler)

    gdsHealth.validate()
    handler.waitForCompletion()
    assertEquals(1, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.BAD)
    agg.unbindStatusHandler(handler)
  }

  @Test
  def testWarning() {
    val handler = new TestHandler(2)
    agg.bindStatusHandler(handler)

    gdsHealth.validate()
    gdsHealth.bindGDSObseventHandler(mock(classOf[GDSObseventHandler]))
    handler.waitForCompletion()
    assertEquals(2, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.WARNING)
    agg.unbindStatusHandler(handler)
  }

  @Test
  def testGood() {
    val handler = new TestHandler(KeywordSource.maxId + 2)
    agg.bindStatusHandler(handler)

    gdsHealth.validate()
    gdsHealth.bindGDSObseventHandler(mock(classOf[GDSObseventHandler]))
    val fact = mock(classOf[KeywordActorsFactory])
    for (source <- (KeywordSource.values - KeywordSource.NONE - KeywordSource.IFS)) {
      when(fact.getSource).thenReturn(source)
      gdsHealth.bindActorFactory(fact)
    }

    gdsHealth.bindHeaderReceiver()
    handler.waitForCompletion()
    assertEquals(KeywordSource.maxId + 1, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.GOOD)
    agg.unbindStatusHandler(handler)
  }

  @Test
  def testUnbind() {
    testGood()
    val handler = new TestHandler(1)
    agg.bindStatusHandler(handler)

    gdsHealth.unbindHeaderReceiver()
    handler.waitForCompletion()
    assertEquals(1, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.WARNING)
    agg.unbindStatusHandler(handler)
  }

  @Test
  def testUnbind2() {
    testUnbind()
    val handler = new TestHandler(1)
    agg.bindStatusHandler(handler)

    gdsHealth.unbindGDSObseventHandler(mock(classOf[GDSObseventHandler]))
    handler.waitForCompletion()
    assertEquals(1, handler.counter.get())
    assertTrue(handler.lastStatusItem.getName == healthName && handler.lastStatusItem.getValue == Health.BAD)
    agg.unbindStatusHandler(handler)
  }

}