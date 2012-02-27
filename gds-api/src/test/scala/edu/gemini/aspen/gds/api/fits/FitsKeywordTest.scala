package edu.gemini.aspen.gds.api.fits

import org.junit.Assert._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FitsKeywordTest extends FunSuite {
  test("correct usage") {
    assertNotNull(FitsKeyword("BITPIX"))
    assertNotNull(FitsKeyword("12345678"))
    assertNotNull(FitsKeyword("ABC_DE-4"))
  }

  test("key using non capital letter") {
    intercept[IllegalArgumentException] {
      FitsKeyword("non")
    }
  }

  test("key too long") {
    intercept[IllegalArgumentException] {
      FitsKeyword("KEYTHATISWAYTOOLONG")
    }
  }

  test("key with spaces") {
    intercept[IllegalArgumentException] {
      FitsKeyword("KEY SP")
    }
  }

  test("key with symbols") {
    intercept[IllegalArgumentException] {
      FitsKeyword("@KEY")
    }
  }

  test("key is null") {
    intercept[IllegalArgumentException] {
      FitsKeyword(null)
    }
  }

  test("key is empty") {
    intercept[IllegalArgumentException] {
      FitsKeyword("")
    }
  }

}