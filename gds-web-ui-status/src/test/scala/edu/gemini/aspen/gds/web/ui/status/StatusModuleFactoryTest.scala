package edu.gemini.aspen.gds.web.ui.status

import org.specs2.mock.Mockito
import org.junit.Test
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.junit.Assert.assertNotNull
import com.vaadin.Application
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.{HealthStatusItem, StatusItem, Health, StatusDatabaseService}
import org.mockito.Matchers.anyInt

class StatusModuleFactoryTest extends Mockito {
  @Test
  def testBuildPanel {
    val statusDB = mock[StatusDatabaseService]
    statusDB.getStatusItem(anyString) answers {
      case x: String => new HealthStatus(x, Health.BAD)
    }
    val obsState = mock[ObservationStateProvider]
    obsState.getObservationsInProgress returns List()
    obsState.getLastDataLabel returns None
    obsState.getLastDataLabel(anyInt) returns None

    val module = new StatusModuleFactory(statusDB, obsState).buildWebModule
    assertNotNull(module)
    val app = mock[Application]
    assertNotNull(module.buildTabContent(app))
  }
}