package edu.gemini.aspen.gds.web.ui.status

import org.specs2.mock.Mockito
import org.junit.{Test, Ignore}
import edu.gemini.aspen.gds.observationstate.impl.ObservationStateImpl
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher
import org.junit.Assert.assertEquals
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.giapi.status.{Health, StatusDatabaseService}
import edu.gemini.aspen.gds.api.CollectionError
import collection.immutable.Set
import edu.gemini.aspen.giapi.data.{ObservationEvent, FitsKeyword}
import edu.gemini.aspen.gds.api.Conversions._
import org.scala_tools.time.Imports._


class PropertyValuesHelperTest extends Mockito {
  @Test
  def testValueFormattingDefaults {
    val statusDB = mock[StatusDatabaseService]
    statusDB.getStatusItem(anyString) returns null
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val module = new PropertyValuesHelper(statusDB, obsState)

    assertEquals(StatusModule.defaultLastDataLabel, module.getLastDataLabel)
    assertEquals(StatusModule.defaultErrors, module.getKeywordsInError)
    assertEquals(StatusModule.defaultMissing, module.getMissingKeywords)
    assertEquals(StatusModule.defaultProcessing, module.getProcessing)
    assertEquals(StatusModule.defaultStatus, module.getStatus)
    assertEquals(StatusModule.defaultTimes, module.getTimes)

  }

  @Ignore
  @Test
  def testValueFormatting {
    val statusDB = mock[StatusDatabaseService]
    statusDB.getStatusItem(anyString) answers {
      case x: String => new HealthStatus(x, Health.BAD)
    }
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val module = new PropertyValuesHelper(statusDB, obsState)

    obsState.startObservation("label")
    obsState.endObservation("label")
    assertEquals("label", module.getLastDataLabel)

    obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD"), CollectionError.GenericError)))
    assertEquals(Set((new FitsKeyword("KEYWORD"), CollectionError.GenericError)).toString, module.getKeywordsInError)

    obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD")))
    assertEquals(Set(new FitsKeyword("KEYWORD")).toString, module.getMissingKeywords)

    obsState.startObservation("label2")
    assertEquals(Set("label2").toString, module.getProcessing)

    assertEquals("BAD", module.getStatus)

    obsState.registerTimes("label", List((ObservationEvent.OBS_PREP, Some(new Duration(1, 2)))))
    assertEquals(Set((ObservationEvent.OBS_PREP, new Duration(1, 2))).toString, module.getTimes)
  }

  @Test
  def testGetStatusStyle {
    val statusDB = mock[StatusDatabaseService]
    statusDB.getStatusItem(anyString) answers {
      case x: String => new HealthStatus(x, Health.BAD)
    }
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val helper = new PropertyValuesHelper(statusDB, obsState)
    assertEquals("gds-red", helper.getStatusStyle)

  }
}