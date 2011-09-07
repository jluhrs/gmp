package edu.gemini.aspen.gds.health

import org.junit.Assert._
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider
import edu.gemini.aspen.giapi.statusservice.{StatusHandlerAggregate, StatusHandlerAggregateImpl, StatusService}
import actors.threadpool.AtomicInteger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import org.mockito.Mockito._
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusHandler}
import org.junit.{After, Before, Test}
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}
import edu.gemini.aspen.gds.obsevent.handler.GDSObseventHandler

class GdsHealthTest {
  val healthName = "gpi:gds:health"

  var counter: AtomicInteger = _
  var latch: CountDownLatch = _
  var gdsHealth: GdsHealth = _
  var lastStatusItem: StatusItem[_] = _
  var statusservice: StatusService = _

  private class TestHandler extends StatusHandler {
    override def getName = "Test Handler for GdsHealthTest"

    override def update[T](item: StatusItem[T]) {
      lastStatusItem = item
      counter.incrementAndGet()
      latch.countDown()
    }

  }

  @Before
  def init() {
    val provider: ActiveMQJmsProvider = new ActiveMQJmsProvider("vm://GdsHealthTest")
    provider.startConnection()
    val agg: StatusHandlerAggregate = new StatusHandlerAggregateImpl

    agg.bindStatusHandler(new TestHandler)
    statusservice = new StatusService(agg, "Status Service", ">", provider)
    statusservice.initialize()
    gdsHealth = new GdsHealth(provider)
  }

  @After
  def shutdown() {
    gdsHealth.invalidate()
    statusservice.stopJms()
  }

  @Test
  def testBad() {
    counter = new AtomicInteger(0)
    latch = new CountDownLatch(1)

    gdsHealth.validate()
    latch.await(1, TimeUnit.SECONDS)
    assertEquals(1, counter.get())
    assertTrue(lastStatusItem.getName == healthName && lastStatusItem.getValue == Health.BAD)
  }

  @Test
  def testWarning() {
    counter = new AtomicInteger(0)
    latch = new CountDownLatch(2)

    gdsHealth.validate()
    gdsHealth.bindGDSObseventHandler(mock(classOf[GDSObseventHandler]))
    latch.await(1, TimeUnit.SECONDS)
    assertEquals(2, counter.get())
    assertTrue(lastStatusItem.getName == healthName && lastStatusItem.getValue == Health.WARNING)
  }

  @Test
  def testGood() {
    counter = new AtomicInteger(0)
    latch = new CountDownLatch(KeywordSource.maxId + 2)

    gdsHealth.validate()
    gdsHealth.bindGDSObseventHandler(mock(classOf[GDSObseventHandler]))
    val fact = mock(classOf[KeywordActorsFactory])
    for (source <- (KeywordSource.values - KeywordSource.NONE - KeywordSource.IFS)) {
      when(fact.getSource).thenReturn(source)
      gdsHealth.bindActorFactory(fact)
    }

    gdsHealth.bindHeaderReceiver()
    latch.await(1, TimeUnit.SECONDS)
    assertEquals(KeywordSource.maxId + 1, counter.get())
    assertTrue(lastStatusItem.getName == healthName && lastStatusItem.getValue == Health.GOOD)
  }

  @Test
  def testUnbind() {
    testGood()
    counter = new AtomicInteger(0)
    latch = new CountDownLatch(1)
    gdsHealth.unbindHeaderReceiver()
    latch.await(1, TimeUnit.SECONDS)
    assertEquals(1, counter.get())
    assertTrue(lastStatusItem.getName == healthName && lastStatusItem.getValue == Health.WARNING)
  }

  @Test
  def testUnbind2() {
    testUnbind()
    counter = new AtomicInteger(0)
    latch = new CountDownLatch(1)
    gdsHealth.unbindGDSObseventHandler(mock(classOf[GDSObseventHandler]))
    latch.await(1, TimeUnit.SECONDS)
    assertEquals(1, counter.get())
    assertTrue(lastStatusItem.getName == healthName && lastStatusItem.getValue == Health.BAD)
  }

}