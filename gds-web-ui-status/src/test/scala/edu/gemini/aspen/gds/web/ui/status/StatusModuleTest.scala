package edu.gemini.aspen.gds.web.ui.status

import org.specs2.mock.Mockito
import org.junit.Assert._
import com.vaadin.Application
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.{Health, StatusDatabaseService}
import edu.gemini.aspen.gds.observationstate.impl.ObservationStateImpl
import edu.gemini.aspen.gds.observationstate.{ObservationStatePublisher, ObservationStateProvider}
import edu.gemini.aspen.gds.api.Conversions._
import collection.immutable.Set.Set1
import edu.gemini.aspen.gds.api.{CollectionError}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent, FitsKeyword}
import org.junit.{Ignore, Test}

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

    @Ignore
    @Test
    def testValueFormattingDefaults {
        val statusDB = mock[StatusDatabaseService]
        statusDB.getStatusItem(anyString) returns null
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

        val module = new StatusModule(statusDB, obsState)

        assertEquals(module.defaultLastDataLabel, module.getLastDataLabel.getValue)
        assertEquals(module.defaultErrors, module.getKeywordsInError.getValue)
        assertEquals(module.defaultMissing, module.getMissingKeywords.getValue)
        assertEquals(module.defaultProcessing, module.getProcessing.getValue)
        assertEquals(module.defaultStatus, module.getStatus.getValue)
        assertEquals(module.defaultTimes, module.getTimes.getValue)

    }

    //todo: fix these tests. Problem with iPojo injection in constructors.
    @Ignore
    @Test
    def testValueFormatting {
        val statusDB = mock[StatusDatabaseService]
        statusDB.getStatusItem(anyString) answers {
            case x: String => new HealthStatus(x, Health.BAD)
        }
        val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

        val module = new StatusModule(statusDB, obsState)

        obsState.startObservation("label")
        obsState.endObservation("label")
        assertEquals("label", module.getLastDataLabel.getValue)

        obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD"), CollectionError.GenericError)))
        assertEquals(new Set1((new FitsKeyword("KEYWORD"), CollectionError.GenericError)).toString, module.getKeywordsInError.getValue)

        obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD")))
        assertEquals(new Set1(new FitsKeyword("KEYWORD")).toString, module.getMissingKeywords.getValue)

        obsState.startObservation("label2")
        assertEquals(new Set1("label2").toString, module.getProcessing.getValue)

        assertEquals("BAD", module.getStatus.getValue)

        obsState.registerTimes("label", List((ObservationEvent.OBS_PREP, Some(new Duration(1, 2)))))
        assertEquals(new Set1((ObservationEvent.OBS_PREP, new Duration(1, 2))).toString, module.getTimes.getValue)
    }
}