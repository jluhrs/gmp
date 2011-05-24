package edu.gemini.aspen.gds.performancemonitoring

import org.scalatest.junit.AssertionsForJUnit
import org.junit.Test

class EventLoggerTest extends AssertionsForJUnit {

  @Test
  def testBasic() {
    val el = new EventLoggerImpl
    el.validate()

    el ! AddEventSet("set")
    el ! Start("set", "hola")
    el ! End("set", "hola")
    el ! Start("set", "chao")
    el ! End("set", "Oops")
    el ! Dump("set")
    el ! DumpAll()

    Thread.sleep(1000)
  }
}