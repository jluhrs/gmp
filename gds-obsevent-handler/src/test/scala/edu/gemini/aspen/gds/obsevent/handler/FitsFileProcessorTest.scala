package edu.gemini.aspen.gds.obsevent.handler

import org.mockito.Mockito._
import org.mockito.Matchers._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import java.io.File
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.mock.MockitoSugar
import java.util.logging.Logger
import edu.gemini.aspen.gds.api.CollectedValue
import edu.gemini.aspen.gds.api.fits.{HeaderItem, Header}
import org.apache.felix.ipojo.handlers.event.publisher.Publisher

@RunWith(classOf[JUnitRunner])
class FitsFileProcessorTest extends FunSuite with MockitoSugar with BeforeAndAfter {
  val propertyHolder = mock[PropertyHolder]
  val tempDir = System.getProperty("java.io.tmpdir")
  when(propertyHolder.getProperty(anyString())).thenReturn(tempDir)

  val registrar = mock[Publisher]
  val dataLabel = new DataLabel("GS-2011.fits")
  val dummyFile = new File(tempDir, dataLabel.getName)

  before {
    dummyFile.createNewFile()
  }

  test("convertToHeaders empty") {
    implicit val LOG = mock[Logger]
    val eventLogger = new ObservationEventLogger
    val fp = new FitsFileProcessor(propertyHolder, eventLogger)

    assertEquals(List(Header(0, Nil)), fp.convertToHeaders(Nil))
  }

  test("convertToHeaders with one header") {
    implicit val LOG = mock[Logger]
    val eventLogger = new ObservationEventLogger
    val fp = new FitsFileProcessor(propertyHolder, eventLogger)

    val cv = List(CollectedValue("KEY", "1.0", "comment", 0))

    assertEquals(List(Header(0, HeaderItem("KEY", "1.0", "comment") :: Nil)), fp.convertToHeaders(cv))

    val cv2 = CollectedValue("KEY", "1.0", "comment", 0) :: CollectedValue("KEY2", "1.0", "comment", 0) :: Nil

    assertEquals(List(Header(0, HeaderItem("KEY", "1.0", "comment") :: HeaderItem("KEY2", "1.0", "comment") :: Nil)), fp.convertToHeaders(cv2))
  }

  test("convertToHeaders with two headers") {
    implicit val LOG = mock[Logger]
    val eventLogger = new ObservationEventLogger
    val fp = new FitsFileProcessor(propertyHolder, eventLogger)

    val cv2 = CollectedValue("KEY", "1.0", "comment", 0) :: CollectedValue("KEY2", "1.0", "comment", 1) :: Nil

    assertEquals(Header(0, HeaderItem("KEY", "1.0", "comment") :: Nil) :: Header(1, HeaderItem("KEY2", "1.0", "comment") :: Nil) :: Nil, fp.convertToHeaders(cv2))
  }

  after {
    dummyFile.delete()
  }
}