package edu.gemini.aspen.gds.web.ui.status

import org.mockito.Mockito._
import edu.gemini.aspen.gds.observationstate.impl.ObservationStateImpl
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher
import org.junit.Assert.assertEquals
import edu.gemini.aspen.giapi.status.impl.HealthStatus
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.giapi.data.ObservationEvent
import edu.gemini.aspen.gds.api.Conversions._
import org.scala_tools.time.Imports._
import edu.gemini.aspen.gmp.top.Top
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.stubbing.Answer
import org.mockito.invocation.InvocationOnMock
import edu.gemini.aspen.giapi.status.{StatusItem, Health, StatusDatabaseService}
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith

@RunWith(classOf[JUnitRunner])
class PropertyValuesHelperTest extends FunSuite with MockitoSugar {
  val top = mock[Top]
  when(top.buildStatusItemName(anyString)).thenReturn("gpitest:gds:health")

  test("Value formatting defaults") {
    val statusDB = mock[StatusDatabaseService]
    when(statusDB.getStatusItem(anyString)).thenReturn(null)
    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val module = new PropertyValuesHelper(statusDB, obsState, top)

    assertEquals(StatusModule.defaultLastDataLabel, module.getLastDataLabel)
    assertEquals(StatusModule.defaultErrors, module.getKeywordsInError)
    assertEquals(StatusModule.defaultMissing, module.getMissingKeywords)
    assertEquals(StatusModule.defaultProcessing, module.getProcessing)
    assertEquals(StatusModule.defaultStatus, module.getStatus)
    assertEquals(StatusModule.defaultTimes, module.getTimes)
  }

  test("Value Formatting") {
    val statusDB = mock[StatusDatabaseService]
    when(statusDB.getStatusItem(anyString)).thenAnswer(new Answer[StatusItem[_]]() {
      def answer(p1: InvocationOnMock) = new HealthStatus(p1.getArguments.apply(0).toString, Health.BAD)
    })
    val obsState = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState, top)

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

  test("Get Processing") {
    val statusDB = mock[StatusDatabaseService]
    val obsState = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState, top)

    obsState.startObservation("label1")
    assertEquals("label1", propertyValuesHelper.getProcessing)
    obsState.startObservation("label2")
    assertEquals("label2, label1", propertyValuesHelper.getProcessing)
  }

  test("GetMissingKeywords") {
    val statusDB = mock[StatusDatabaseService]
    val obsState = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState, top)
    obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD")))
    assertEquals("KEYWORD", propertyValuesHelper.getMissingKeywords)

    obsState.registerMissingKeyword("label", List(new FitsKeyword("KEYWORD2")))
    assertEquals("KEYWORD2, KEYWORD", propertyValuesHelper.getMissingKeywords)
  }

  test("GetKeywordsInError") {
    val statusDB = mock[StatusDatabaseService]
    val obsState = new ObservationStateImpl(mock[ObservationStatePublisher])

    val propertyValuesHelper = new PropertyValuesHelper(statusDB, obsState, top)

    obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD"), CollectionError.GenericError)))
    assertEquals("KEYWORD", propertyValuesHelper.getKeywordsInError)

    obsState.registerCollectionError("label", List((new FitsKeyword("KEYWORD2"), CollectionError.GenericError)))
    assertEquals("KEYWORD2, KEYWORD", propertyValuesHelper.getKeywordsInError)
  }

  test("GetStatusStyle") {
    val statusDB = mock[StatusDatabaseService]
    when(statusDB.getStatusItem(anyString)).thenAnswer(new Answer[StatusItem[_]]() {
      def answer(p1: InvocationOnMock) = new HealthStatus(p1.getArguments.apply(0).toString, Health.BAD)
    })

    val obsState: ObservationStateImpl = new ObservationStateImpl(mock[ObservationStatePublisher])

    val helper = new PropertyValuesHelper(statusDB, obsState, top)
    assertEquals("gds-red", helper.getStatusStyle)

    when(statusDB.getStatusItem(anyString)).thenAnswer(new Answer[StatusItem[_]]() {
      def answer(p1: InvocationOnMock) = new HealthStatus(p1.getArguments.apply(0).toString, Health.WARNING)
    })

    assertEquals("gds-orange", helper.getStatusStyle)
    when(statusDB.getStatusItem(anyString)).thenAnswer(new Answer[StatusItem[_]]() {
      def answer(p1: InvocationOnMock) = new HealthStatus(p1.getArguments.apply(0).toString, Health.GOOD)
    })
    assertEquals("gds-green", helper.getStatusStyle)
  }
}