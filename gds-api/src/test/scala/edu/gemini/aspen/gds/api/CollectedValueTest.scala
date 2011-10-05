package scala.edu.gemini.aspen.gds.api

import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api.{CollectionError, ErrorCollectedValue, CollectedValue}
import scala.collection._

class CollectedValueTest {

  @Test
  def testBuilding() {
    // Build with string
    assertNotNull(CollectedValue("keyword", "strValue", "comment", 0))
    // Build with int
    assertNotNull(CollectedValue("keyword", 1, "comment", 0))
    // Build with double
    assertNotNull(CollectedValue("keyword", 1.1, "comment", 0))
    // Note this cannot compile
    //assertNotNull(CollectedValue("keyword", new Date(), "comment", 0))
  }

  @Test
  def testPatternMatchingString() {
    // Build with string
    val cv = CollectedValue("keyword", "strValue", "comment", 0)
    cv match {
      case CollectedValue(keyword, value, comment, index) => {
        assertEquals(new FitsKeyword("keyword"), keyword)
        assertEquals("strValue", value)
        assertEquals("comment", comment)
        assertEquals(0, index)
      }
      case _ => fail("Should match")
    }
  }

  @Test
  def testPatternMatchingInteger() {
    // Build with string
    val cv = CollectedValue("keyword", 99, "comment", 0)
    cv match {
      case CollectedValue(keyword, value, comment, index) => {
        assertEquals(new FitsKeyword("keyword"), keyword)
        assertEquals(99, value)
        assertEquals("comment", comment)
        assertEquals(0, index)
      }
      case _ => fail("Should match")
    }
  }

  @Test
  def testPatternMatchingDouble() {
    // Build with string
    val cv = CollectedValue("keyword", 1.1, "comment", 0)
    cv match {
      case CollectedValue(keyword, value, comment, index) => {
        assertEquals(new FitsKeyword("keyword"), keyword)
        assertEquals(1.1, value)
        assertEquals("comment", comment)
        assertEquals(0, index)
      }
      case _ => fail("Should match")
    }
  }

  @Test
  def testPatternMatchingInList() {
    // Build with string
    val cv = CollectedValue("keyword", 1.1, "comment", 0)
    val list = immutable.List(cv)
    list match {
      case CollectedValue(keyword, value, comment, index) :: Nil => {
        assertEquals(new FitsKeyword("keyword"), keyword)
        assertEquals(1.1, value)
        assertEquals("comment", comment)
        assertEquals(0, index)
      }
      case _ => fail("Should match")
    }
  }

  @Test
  def testPatternMatchingError() {
    // Build with string
    val ev = ErrorCollectedValue("keyword", CollectionError.MandatoryRequired, "comment", 0)
    assertTrue(ev.isError)
    ev match {
      case ErrorCollectedValue(keyword, CollectionError.MandatoryRequired, comment, index) => {
        assertEquals(new FitsKeyword("keyword"), keyword)
        assertEquals("comment", comment)
        assertEquals(0, index)
      }
      case _ => fail("Should match")
    }

    ev match {
      case ErrorCollectedValue(keyword, error, comment, index) => {
        assertEquals(new FitsKeyword("keyword"), keyword)
        assertEquals(CollectionError.MandatoryRequired, error)
        assertEquals("comment", comment)
        assertEquals(0, index)
      }
      case _ => fail("Should match")
    }
  }

}