package edu.gemini.aspen.gds.web.ui.status

import org.mockito.Mockito._
import org.junit.Test
import org.junit.Assert.assertNotNull
import com.vaadin.Application
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.{Health, StatusDatabaseService}
import edu.gemini.aspen.gmp.top.Top
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.stubbing.Answer
import edu.gemini.aspen.giapi.status.{StatusItem, Health, StatusDatabaseService}
import org.mockito.invocation.InvocationOnMock

@RunWith(classOf[JUnitRunner])
class StatusModuleFactoryTest extends FunSuite with MockitoSugar {
  /*test("BuildPanel") {
    val statusDB = mock[StatusDatabaseService]
    when(statusDB.getStatusItem(anyString)).thenAnswer(new Answer[StatusItem[_]]() {
      def answer(p1: InvocationOnMock) = new HealthStatus(p1.getArguments.apply(0).toString, Health.BAD)
    })
    val obsState = mock[ObservationStateProvider]
    when(obsState.getObservationsInProgress).thenReturn(Nil)
    when(obsState.getLastDataLabel).thenReturn(None)
    when(obsState.getLastDataLabel(anyInt)).thenReturn(None)

    val top=mock[Top]
    when(top.buildStatusItemName(anyString)).thenReturn("gpitest:gds:health")

    val module = new StatusModuleFactory(statusDB, obsState, top).buildWebModule
    assertNotNull(module)
    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }*/
}