package edu.gemini.aspen.gds.performancemonitoring

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import org.scala_tools.time.Imports._
import scala.Some

class EventLoggerTest extends AssertionsForJUnit {

  private def check(value: Duration, target: Duration, margin: Duration) = {
    ((value > target) && (value - target) < margin) ||
      ((value < target) && (target - value) < margin)
  }

  @Test
  def testBasic() {
    val delay = 500;
    val el = new EventLoggerImpl
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

    (el !? (1000, Retrieve("set"))) match {
      case Some(y) => {
        val x = y.asInstanceOf[scala.collection.Map[AnyRef, Option[Duration]]]
        assert(check(x("hola").get, 500.millis, 200.millis), "Time is: " + x("hola").get) //+- 200 ms
        assert(x("chao").isEmpty) //doesn't end
        assert(x("Oops").isEmpty) //doesn't start
      }
      case _ => fail()
    }

    (el !? (1000, RetrieveEventAverage("hola"))) match {
      case Some(x: Duration) => assert(check(x, 500.millis, 100.millis), "Time is: " + x)
      case _ => fail()
    }
    (el !? (1000, RetrieveEventAverage("chao"))) match {
      case Some(x: Duration) => assert(x.compare(0.millis) == 0, "Time is: " + x)
      case _ => fail()
    }
  }

}