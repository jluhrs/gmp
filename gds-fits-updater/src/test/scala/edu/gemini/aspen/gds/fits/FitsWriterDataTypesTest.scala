package edu.gemini.aspen.gds.fits

import org.junit.Assert._
import java.io.File
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import com.google.common.io.Files
import edu.gemini.aspen.gds.api.fits.{HeaderItem, Header}
import edu.gemini.aspen.gds.api.Conversions._
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfter, FunSuite}
import edu.gemini.aspen.gds.api.FitsType

@RunWith(classOf[JUnitRunner])
class FitsWriterDataTypesTest extends FunSuite with BeforeAndAfterEach {
  val destFile = File.createTempFile("test", ".fits")

  override def afterEach() {
    if (destFile.exists()) destFile.delete()
  }

  // Creates a new header with a single keyword
  def createHeadersWithKeyword[T](value: T)(implicit _type: FitsType[T]): Header = Header(0, List(HeaderItem("KEY", value, "comment")))

  // Verifies that a header contains all the original keywords plus new keywords
  def toMap(header: Header) = header.keywords map {
    k => k.keywordName.key -> k.value
  } toMap

  test("update keyword with string item") {
    val originalFile = new File(classOf[FitsWriterDataTypesTest].getResource("sample1.fits").toURI)
    val fitsFile = new FitsWriter(originalFile)

    val headerUpdate = createHeadersWithKeyword("value")

    fitsFile.updateHeader(headerUpdate, destFile)

    val updatedFitsFile = new FitsReader(destFile)
    val updatedHeader = updatedFitsFile.header(0).get

    assertTrue(updatedHeader.containsKey("KEY"))
    assertEquals("value", toMap(updatedHeader).get("KEY").get)
  }

  test("update keyword with int item") {
    val originalFile = new File(classOf[FitsWriterDataTypesTest].getResource("sample1.fits").toURI)
    val fitsFile = new FitsWriter(originalFile)

    val headerUpdate = createHeadersWithKeyword(1)

    fitsFile.updateHeader(headerUpdate, destFile)

    val updatedFitsFile = new FitsReader(destFile)
    val updatedHeader = updatedFitsFile.header(0).get

    assertTrue(updatedHeader.containsKey("KEY"))
    assertEquals("1", toMap(updatedHeader).get("KEY").get)
  }

  test("update keyword with true boolean item") {
    val originalFile = new File(classOf[FitsWriterDataTypesTest].getResource("sample1.fits").toURI)
    val fitsFile = new FitsWriter(originalFile)

    val headerUpdate = createHeadersWithKeyword(true)

    fitsFile.updateHeader(headerUpdate, destFile)

    val updatedFitsFile = new FitsReader(destFile)
    val updatedHeader = updatedFitsFile.header(0).get

    assertTrue(updatedHeader.containsKey("KEY"))
    assertEquals("T", toMap(updatedHeader).get("KEY").get)
  }

  test("update keyword with false boolean item") {
    val originalFile = new File(classOf[FitsWriterDataTypesTest].getResource("sample1.fits").toURI)
    val fitsFile = new FitsWriter(originalFile)

    val headerUpdate = createHeadersWithKeyword(false)

    fitsFile.updateHeader(headerUpdate, destFile)

    val updatedFitsFile = new FitsReader(destFile)
    val updatedHeader = updatedFitsFile.header(0).get

    assertTrue(updatedHeader.containsKey("KEY"))
    assertEquals("F", toMap(updatedHeader).get("KEY").get)
  }

}