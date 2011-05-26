package edu.gemini.aspen.gds.performancemonitoring

import org.scalatest.junit.AssertionsForJUnit
import org.scala_tools.time.Imports._
import scala.Some
import org.junit.{Before, Test}

class EventLoggerTest extends AssertionsForJUnit {

  private def check(value: Duration, target: Duration, margin: Duration) = {
    ((value >= target) && (value - target) < margin) ||
      ((value <= target) && (target - value) < margin)
  }

  val delay = 100;
  var el: EventLoggerImpl = _

  @Before
  def setup() {
    el = new EventLoggerImpl
    el.validate()

    el ! AddEventSet("set")
    el ! Start("set", "hola")
    Thread.sleep(delay)
    el ! End("set", "hola")

    el ! AddEventSet("otroset")
    el ! Start("otroset", "hola")
    Thread.sleep(delay)
    el ! End("otroset", "hola")

    el ! AddEventSet("otrosetmas")
    el ! Start("otrosetmas", "hola")
    Thread.sleep(delay)
    el ! End("otrosetmas", "hola")

    el ! Start("set", "chao")
    el ! End("set", "Oops")
  }

  @Test
  def testRetrieve() {

    (el !? (1000, Retrieve("set"))) match {
      case Some(y) => {
        val x = y.asInstanceOf[scala.collection.Map[AnyRef, Option[Duration]]]
        assert(check(x("hola").get, delay.millis, 100.millis), "Time is: " + x("hola").get)
        assert(x("chao").isEmpty) //doesn't end
        assert(x("Oops").isEmpty) //doesn't start
      }
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEvent() {
    (el !? (1000, RetrieveEvent("set", "hola"))) match {
      case Some(Some(x: Duration)) => assert(check(x, delay.millis, 50.millis), "Time is: " + x)
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEventEmpty() {
    (el !? (1000, RetrieveEvent("set", "chao"))) match {
      case Some(None) =>
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEventAverage() {
    (el !? (1000, RetrieveEventAverage("hola"))) match {
      case Some(x: Duration) => assert(check(x, delay.millis, 50.millis), "Time is: " + x)
      case _ => fail()
    }
  }

  @Test
  def testRetrieveEventAverageEmpty() {
    (el !? (1000, RetrieveEventAverage("chao"))) match {
      case Some(x: Duration) => assert(x.compare(0.millis) == 0, "Time is: " + x)
      case _ => fail()
    }
  }

  @Test
  def testCheck() {
    (el !? (1000, Check("set", "hola", 600))) match {
      case Some(x: Boolean) => assert(x)
      case _ => fail()
    }
  }

  @Test
  def testCheckEmpty() {
    (el !? (1000, Check("set", "chao", 600))) match {
      case Some(x: Boolean) => assert(!x)
      case _ => fail()
    }
  }

}