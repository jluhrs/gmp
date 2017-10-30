package edu.gemini.gpi.observationstatus

import edu.gemini.aspen.gds.api.GDSStartObservation
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.giapi.status.setter.StatusSetter
import edu.gemini.aspen.giapi.status.{StatusDatabaseService, StatusItem}
import edu.gemini.gmp.top.Top
import org.junit.Assert._
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mockito._
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
class ObservationEventsListenerTest extends FunSuite with MockitoSugar {
  test("export datalabel") {
    val top = mock[Top]
    when(top.buildStatusItemName("ifs:observationDataLabel")).thenReturn("gpi:ifs:observationDataLabel")
    when(top.buildStatusItemName("massLastUpdate")).thenReturn("gpi:massLastUpdate")
    when(top.buildStatusItemName("massMinsSince")).thenReturn("gpi:massMinsSince")
    when(top.buildStatusItemName("dimmLastUpdate")).thenReturn("gpi:dimmLastUpdate")
    when(top.buildStatusItemName("dimmMinsSince")).thenReturn("gpi:dimmMinsSince")
    val setter = mock[StatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("NEW_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter, times(3)).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gpi:ifs:observationDataLabel")
    assertEquals(argument.getValue.getValue, "NEW_LABEL")
  }
  test("export datalabel verify label name") {
    val top = mock[Top]
    when(top.buildStatusItemName("ifs:observationDataLabel")).thenReturn("gpi:ifs:observationDataLabel")
    when(top.buildStatusItemName("massLastUpdate")).thenReturn("gpi:massLastUpdate")
    when(top.buildStatusItemName("massMinsSince")).thenReturn("gpi:massMinsSince")
    when(top.buildStatusItemName("dimmLastUpdate")).thenReturn("gpi:dimmLastUpdate")
    when(top.buildStatusItemName("dimmMinsSince")).thenReturn("gpi:dimmMinsSince")
    val setter = mock[StatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("ANOTHER_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter, times(3)).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gpi:ifs:observationDataLabel")
    assertEquals(argument.getValue.getValue, "ANOTHER_LABEL")
  }
  test("export datalabel verify channel name") {
    val top = mock[Top]
    when(top.buildStatusItemName("ifs:observationDataLabel")).thenReturn("gmp:ifs:observationDataLabel")
    when(top.buildStatusItemName("massLastUpdate")).thenReturn("gpi:massLastUpdate")
    when(top.buildStatusItemName("massMinsSince")).thenReturn("gpi:massMinsSince")
    when(top.buildStatusItemName("dimmLastUpdate")).thenReturn("gpi:dimmLastUpdate")
    when(top.buildStatusItemName("dimmMinsSince")).thenReturn("gpi:dimmMinsSince")
    val setter = mock[StatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("ANOTHER_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter, times(3)).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gmp:ifs:observationDataLabel")
    assertEquals(argument.getValue.getValue, "ANOTHER_LABEL")
  }
}
