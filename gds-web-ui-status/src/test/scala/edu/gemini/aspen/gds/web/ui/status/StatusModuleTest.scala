package edu.gemini.aspen.gds.web.ui.status

import org.specs2.mock.Mockito
import org.junit.Test
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.junit.Assert.assertNotNull
import com.vaadin.Application
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.{StatusItem, Health, StatusDatabaseService}
import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}

class StatusModuleTest extends Mockito {
    @Test
    def testBuildPanel {
        val statusDB = mock[StatusDatabaseService]
        statusDB.getStatusItem(anyString) answers {
            case x: String => new HealthStatus(x, Health.BAD)
        }
        val obsState = mock[ObservationStateProvider]
        obsState.getObservationsInProgress returns List()
        obsState.getLastDataLabel returns None

        // mock configuration service
        val module = new StatusModule(statusDB, obsState)

        val app = mock[Application]
        assertNotNull(module.buildTabContent(app))
    }
}