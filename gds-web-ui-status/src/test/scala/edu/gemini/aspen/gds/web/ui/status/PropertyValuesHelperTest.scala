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

  @Test
  def testValueFormatting {
    val statusDB = mock[StatusDatabaseService]
    statusDB.getStatusItem(anyString) answers {
      case x: String => new HealthStatus(x, Health.BAD)
    }
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState)

    obsState.startObservation("label")
    obsState.endObservation("label")
    assertEquals("label", propertyValuesHelper.getLastDataLabel)

    obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD"), CollectionError.GenericError)))
    assertEquals("KEYWORD", propertyValuesHelper.getKeywordsInError)

    obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD")))
    assertEquals("KEYWORD", propertyValuesHelper.getMissingKeywords)

    obsState.startObservation("label2")
    assertEquals("label2", propertyValuesHelper.getProcessing)

    assertEquals("BAD", propertyValuesHelper.getStatus)
    obsState.endObservation("label2")

    obsState.registerTimes("label2", List((ObservationEvent.OBS_PREP, Some(new Duration(1, 2)))))
    assertEquals("1[ms]", propertyValuesHelper.getTimes)
  }

  @Test
  def testGetProcessing {
    val statusDB = mock[StatusDatabaseService]
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState)

    obsState.startObservation("label1")
    assertEquals("label1", propertyValuesHelper.getProcessing)
    obsState.startObservation("label2")
    assertEquals("label2, label1", propertyValuesHelper.getProcessing)
  }

  @Test
  def testGetMissingKeywords {
    val statusDB = mock[StatusDatabaseService]
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState)
    obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD")))
    assertEquals("KEYWORD", propertyValuesHelper.getMissingKeywords)

    obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD2")))
    assertEquals("KEYWORD2, KEYWORD", propertyValuesHelper.getMissingKeywords)
  }

  @Test
  def testGetKeywordsInError {
    val statusDB = mock[StatusDatabaseService]
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState)

    obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD"), CollectionError.GenericError)))
    assertEquals("KEYWORD", propertyValuesHelper.getKeywordsInError)

    obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD2"), CollectionError.GenericError)))
    assertEquals("KEYWORD2, KEYWORD", propertyValuesHelper.getKeywordsInError)
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

    statusDB.getStatusItem(anyString) answers {
      case x: String => new HealthStatus(x, Health.WARNING)
    }

    assertEquals("gds-orange", helper.getStatusStyle)
    statusDB.getStatusItem(anyString) answers {
      case x: String => new HealthStatus(x, Health.GOOD)
    }
    assertEquals("gds-green", helper.getStatusStyle)
  }
}