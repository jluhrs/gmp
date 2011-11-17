package edu.gemini.aspen.gds.web.ui.status

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.junit.Assert._
import com.vaadin.Application
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.{Health, StatusDatabaseService}
import edu.gemini.aspen.gds.observationstate.{ObservationStateProvider}
import edu.gemini.aspen.gds.api.Conversions._
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock

class StatusModuleTest extends FunSuite with MockitoSugar {
  test("build panel") {
    val statusDB = mock[StatusDatabaseService]
    when(statusDB.getStatusItem(anyString)) thenAnswer (new Answer[HealthStatus](){
      def answer(in: InvocationOnMock) = new HealthStatus(in.getArguments.head.toString, Health.BAD)
    })
    val obsState = mock[ObservationStateProvider]
    when(obsState.getObservationsInProgress) thenReturn Nil
    when(obsState.getLastDataLabel) thenReturn None
    when(obsState.getLastDataLabel(anyInt)) thenReturn None

    // mock configuration service
    val module = new StatusModule(statusDB, obsState)

    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }

  test("build label") {
    assertEquals("Some label", StatusModule.buildLabel("Some label").getCaption)
  }

}