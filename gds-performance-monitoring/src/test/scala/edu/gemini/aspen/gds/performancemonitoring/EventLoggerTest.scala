package edu.gemini.aspen.gds.performancemonitoring

import org.scalatest.junit.AssertionsForJUnit
import scala.Some
import org.junit.{Test, Before}
import org.joda.time.{DateTime, Duration}

class EventLoggerTest extends AssertionsForJUnit {

  private def check(value: Duration, target: Duration, margin: Duration) = {
    ((value.isLongerThan(target) || value == target) && (value.minus(target).isShorterThan(margin)) ||
      ((value.isShorterThan(target) || value == target) && (target.minus(value)).isShorterThan(margin)))
  }

  private def busyWait(value: Duration) = {
    var start =  DateTime.now
    while ( new Duration(start, DateTime.now).compareTo(value) < 0 ) {}
  }

  val delay = new Duration(100)
  val precision = new Duration(100)
  var el: EventLogger[String, String] = _

  var holaInterval: Duration = _

  @Before
  def setup() {
    el = new EventLogger

    el.addEventSet("set")
    el.start("set", "hola")
    val startTime = DateTime.now
    busyWait(delay)
    el.end("set", "hola")
    holaInterval = new Duration(startTime, DateTime.now)

    el.addEventSet("otroset")
    el.start("otroset", "hola")
    busyWait(delay)
    el.end("otroset", "hola")

    el.addEventSet("otrosetmas")
    el.start("otrosetmas", "hola")
    busyWait(delay)
    el.end("otrosetmas", "hola")

    el.start("set", "chao")
    el.end("set", "Oops")
  }

  @Test
  def testRetrieve() {

    val x = el.retrieve("set")
    assert(check(x("hola").get, holaInterval, precision), "Time is: " + x("hola").get)
    assert(x("chao").isEmpty) //doesn't end
    assert(x("Oops").isEmpty) //doesn't start
  }

  @Test
  def testRetrieveEvent() {
    el.retrieve("set", "hola") match {
      case Some(x: Duration) => assert(check(x, holaInterval, precision), "Time is: " + x)
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEventEmpty() {
    el.retrieve("set", "chao") match {
      case None =>
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEventAverage() {
    el.average("hola") match {
      case Some(x: Duration) => assert(check(x, delay, precision), "Time is: " + x)
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEventAverageEmpty() {
    el.average("chao") match {
      case None =>
      case _ => fail()
    }
  }

  @Test
  def testCheck() {
    assert(el.check("set", "hola", 600))
  }

  @Test
  def testCheckEmpty() {
    assert(!el.check("set", "chao", 600))
  }

}