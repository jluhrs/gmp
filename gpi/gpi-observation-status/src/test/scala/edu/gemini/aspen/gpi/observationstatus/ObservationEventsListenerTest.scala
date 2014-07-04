package edu.gemini.aspen.gpi.observationstatus

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.giapi.data.DataLabel
import org.mockito.Mockito._
import edu.gemini.aspen.gds.api.GDSStartObservation
import org.mockito.ArgumentCaptor
import edu.gemini.aspen.giapi.status.{StatusDatabaseService, StatusItem}
import edu.gemini.aspen.giapi.status.setter.IStatusSetter

@RunWith(classOf[JUnitRunner])
class ObservationEventsListenerTest extends FunSuite with MockitoSugar {
  test("export datalabel") {
    val top = mock[Top]
    when(top.buildStatusItemName("ifs:observationDataLabel")).thenReturn("gpi:ifs:observationDataLabel")
    val setter = mock[IStatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("NEW_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gpi:ifs:observationDataLabel")
    assertEquals(argument.getValue.getValue, "NEW_LABEL")
  }
  test("export datalabel verify label name") {
    val top = mock[Top]
    when(top.buildStatusItemName("ifs:observationDataLabel")).thenReturn("gpi:ifs:observationDataLabel")
    val setter = mock[IStatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("ANOTHER_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gpi:ifs:observationDataLabel")
    assertEquals(argument.getValue.getValue, "ANOTHER_LABEL")
  }
  test("export datalabel verify channel name") {
    val top = mock[Top]
    when(top.buildStatusItemName("ifs:observationDataLabel")).thenReturn("gmp:ifs:observationDataLabel")
    val setter = mock[IStatusSetter]
    val statusDB = mock[StatusDatabaseService]

    val listener = new ObservationEventsListener(top, setter, statusDB)
    listener.gdsEvent(GDSStartObservation(new DataLabel("ANOTHER_LABEL")))

    val argument = ArgumentCaptor.forClass(classOf[StatusItem[String]])

    verify(setter).setStatusItem(argument.capture())
    assertEquals(argument.getValue.getName, "gmp:ifs:observationDataLabel")
    assertEquals(argument.getValue.getValue, "ANOTHER_LABEL")
  }
}
