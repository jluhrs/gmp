package edu.gemini.aspen.gds.performancemonitoring

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test
import scala.Some
import org.scala_tools.time.Imports._

class EventLoggerTest extends AssertionsForJUnit {

  @Test
  def testBasic() {
    val el = new EventLoggerImpl
    el.validate()

    el ! AddEventSet("set")
    el ! Start("set", "hola")
    Thread.sleep(500)
    el ! End("set", "hola")
    el ! Start("set", "chao")
    el ! End("set", "Oops")
    el.addEventSet("otro set")

    (el !? (1000, Retrieve("set"))) match {
      case Some(y) => {
        val x = y.asInstanceOf[scala.collection.Map[Any, Option[Duration]]]
        assert((x("hola").get - 250.millis) > 0.millis && ((x("hola").get - 750.millis) < 0.millis), "Time is: " + x("hola").get) //+- 50 ms
        assert(x("chao").isEmpty) //doesn't end
        assert(x("Oops").isEmpty) //doesn't start
      }
      case _ => fail()
    }
  }

}