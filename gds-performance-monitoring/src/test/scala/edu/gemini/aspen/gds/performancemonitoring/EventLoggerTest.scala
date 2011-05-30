package edu.gemini.aspen.gds.performancemonitoring

import org.scalatest.junit.AssertionsForJUnit
import org.scala_tools.time.Imports._
import scala.Some
import org.junit.{Test, Before}

class EventLoggerTest extends AssertionsForJUnit {

  private def check(value: Duration, target: Duration, margin: Duration) = {
    ((value >= target) && (value - target) < margin) ||
      ((value <= target) && (target - value) < margin)
  }

  val delay = 100;
  var el: EventLogger = _

  @Before
  def setup() {
    el = new EventLogger

    el.addEventSet("set")
    el.start("set", "hola")
    Thread.sleep(delay)
    el.end("set", "hola")

    el.addEventSet("otroset")
    el.start("otroset", "hola")
    Thread.sleep(delay)
    el.end("otroset", "hola")

    el.addEventSet("otrosetmas")
    el.start("otrosetmas", "hola")
    Thread.sleep(delay)
    el.end("otrosetmas", "hola")

    el.start("set", "chao")
    el.end("set", "Oops")
  }

  @Test
  def testRetrieve() {

    el.retrieve("set") match {
      case x: scala.collection.Map[AnyRef, Option[Duration]] => {
        assert(check(x("hola").get, delay.millis, 100.millis), "Time is: " + x("hola").get)
        assert(x("chao").isEmpty) //doesn't end
        assert(x("Oops").isEmpty) //doesn't start
      }
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEvent() {
    el.retrieve("set", "hola") match {
      case Some(x: Duration) => assert(check(x, delay.millis, 50.millis), "Time is: " + x)
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
      case Some(x: Duration) => assert(check(x, delay.millis, 50.millis), "Time is: " + x)
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
    el.check("set", "hola", 600) match {
      case x: Boolean => assert(x)
      case _ => fail()
    }
  }

  @Test
  def testCheckEmpty() {
    el.check("set", "chao", 600) match {
      case x: Boolean => assert(!x)
      case _ => fail()
    }
  }

}