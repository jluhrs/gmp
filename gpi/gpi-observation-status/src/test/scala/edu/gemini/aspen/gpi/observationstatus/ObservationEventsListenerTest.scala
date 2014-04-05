package edu.gemini.aspen.gpi.observationstatus

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter
import edu.gemini.aspen.giapi.data.DataLabel
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.GDSStartObservation
import org.mockito.ArgumentCaptor
import edu.gemini.aspen.giapi.status.{StatusDatabaseService, StatusItem}

@RunWith(classOf[JUnitRunner])
class ObservationEventsListenerTest extends FunSuite with MockitoSugar {
  test("export datalabel") {
    val top = mock[Top]
    when(top.buildStatusItemName("observationDataLabel")).thenReturn("gpi:observationDataLabel")
    val statusDB = mock[StatusDatabaseService]
    val setter = mock[IStatusSetter]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("NEW_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gpi:observationDataLabel")
    assertEquals(argument.getValue.getValue, "NEW_LABEL")
  }
  test("export datalabel verify label name") {
    val top = mock[Top]
    when(top.buildStatusItemName("observationDataLabel")).thenReturn("gpi:observationDataLabel")
    val setter = mock[IStatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("ANOTHER_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gpi:observationDataLabel")
    assertEquals(argument.getValue.getValue, "ANOTHER_LABEL")
  }
  test("export datalabel verify channel name") {
    val top = mock[Top]
    when(top.buildStatusItemName("observationDataLabel")).thenReturn("gmp:observationDataLabel")
    val setter = mock[IStatusSetter]
    val statusDB = mock[StatusDatabaseService]
    
    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("ANOTHER_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gmp:observationDataLabel")
    assertEquals(argument.getValue.getValue, "ANOTHER_LABEL")
  }
}
