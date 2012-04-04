package edu.gemini.aspen.gmp.web.ui.status

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

@RunWith(classOf[JUnitRunner])
class GMPStatusAppTest extends FunSuite {
  test("Construction") {
    assertNotNull(new GMPStatusApp(null))
  }
}