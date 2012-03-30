package edu.gemini.aspen.gds.obsevent.handler

import org.mockito.Mockito._
import org.mockito.Matchers._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.keywords.database.impl.KeywordsDatabaseImpl
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import java.io.File
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, OneInstancePerTest, FunSuite}
import java.util.concurrent.TimeUnit
import org.apache.felix.ipojo.handlers.event.publisher.Publisher
import edu.gemini.aspen.gds.api.{GDSEndObservation, GDSStartObservation, CompositeErrorPolicyImpl, KeywordValueActor}

@RunWith(classOf[JUnitRunner])
class GDSObseventHandlerImplTest extends FunSuite with OneInstancePerTest with BeforeAndAfter {
  val actorsFactory = mock(classOf[CompositeActorsFactory])
  val propertyHolder = mock(classOf[PropertyHolder])
  val keywordsDatabase = new KeywordsDatabaseImpl()
  val tempDir = System.getProperty("java.io.tmpdir")
  when(propertyHolder.getProperty(anyString())).thenReturn(tempDir)

  val dataLabel = new DataLabel("GS-2011.fits")
  val dummyFile = new File(tempDir, dataLabel.getName)
  val mockPublisher = mock(classOf[Publisher])

  before {
    dummyFile.createNewFile()
  }

  private val observationHandler = new GDSObseventHandlerImpl(actorsFactory, keywordsDatabase, new CompositeErrorPolicyImpl(), propertyHolder) {
    publisher = mockPublisher
  }

  test("verify all events") {

    for (evt <- ObservationEvent.values()) {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

      observationHandler.onObservationEvent(evt, dataLabel)

      sleep(300)

      // verify mock
      verify(actorsFactory).buildActors(evt, dataLabel)
    }
    verify(mockPublisher).sendData(GDSStartObservation(dataLabel))
    sleep(500)
    verify(mockPublisher).sendData(GDSEndObservation(dataLabel))
    //verify(registrar).endObservation(dataLabel)
  }

  test("with a missing event") {
    when(actorsFactory.buildActors(any[ObservationEvent], any[DataLabel])).thenReturn(List[KeywordValueActor]())

    for {evt <- ObservationEvent.values()
         if (evt != ObservationEvent.EXT_END_OBS && evt != ObservationEvent.EXT_START_OBS && evt != ObservationEvent.OBS_START_ACQ)} {
      observationHandler.onObservationEvent(evt, dataLabel)
    }
    sleep(300)
    verify(mockPublisher).sendData(GDSStartObservation(dataLabel))
    verify(mockPublisher, times(0)).sendData(GDSEndObservation(dataLabel))
//    verify(registrar).startObservation(dataLabel)
    //    verify(registrar, times(0)).endObservation(dataLabel)
    sleep(6500)
    verify(mockPublisher).sendData(GDSEndObservation(dataLabel))
    //    verify(registrar).endObservation(dataLabel)
  }

  test("with slow event") {
    when(actorsFactory.buildActors(any[ObservationEvent], any[DataLabel])).thenReturn(List[KeywordValueActor]())

    for (evt <- ObservationEvent.values()) {
      if (evt != ObservationEvent.OBS_START_ACQ) {
        observationHandler.onObservationEvent(evt, dataLabel)
      }
    }
    sleep(300)
    verify(mockPublisher).sendData(GDSStartObservation(dataLabel))
    verify(mockPublisher, times(0)).sendData(GDSEndObservation(dataLabel))
//    verify(registrar).startObservation(dataLabel)
//    verify(registrar, times(0)).endObservation(dataLabel)
    sleep(500)
    observationHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel)
    sleep(1500)
    verify(mockPublisher).sendData(GDSEndObservation(dataLabel))
//    verify(registrar).endObservation(dataLabel)
  }

  test("without start/end transaction") {
    when(actorsFactory.buildActors(any[ObservationEvent], any[DataLabel])).thenReturn(List[KeywordValueActor]())

    for {evt <- ObservationEvent.values()
         if (evt != ObservationEvent.EXT_END_OBS && evt != ObservationEvent.EXT_START_OBS)
    } {
      observationHandler.onObservationEvent(evt, dataLabel)
    }
    sleep(300)
//    verify(registrar).startObservation(dataLabel)
    verify(mockPublisher).sendData(GDSStartObservation(dataLabel))
    sleep(1500)
    verify(mockPublisher).sendData(GDSEndObservation(dataLabel))
//    verify(registrar).endObservation(dataLabel)
  }

  test("with start transaction but no end transaction") {
    when(actorsFactory.buildActors(any[ObservationEvent], any[DataLabel])).thenReturn(List[KeywordValueActor]())

    // Simulate an ext start obs arriving
    observationHandler.onObservationEvent(ObservationEvent.EXT_START_OBS, dataLabel)

    for {evt <- ObservationEvent.values()
         if (evt != ObservationEvent.EXT_END_OBS && evt != ObservationEvent.EXT_START_OBS)} {
      observationHandler.onObservationEvent(evt, dataLabel)
    }
    sleep(500)
    verify(mockPublisher).sendData(GDSStartObservation(dataLabel))
//    verify(registrar).startObservation(dataLabel)
    sleep(500)
    verify(mockPublisher, times(0)).sendData(GDSEndObservation(dataLabel))
//    verify(registrar, times(0)).endObservation(dataLabel)
  }

  test("with start transaction and end transaction") {
    // Simulate an ext start obs arriving
    when(actorsFactory.buildActors(any[ObservationEvent], any[DataLabel])).thenReturn(List[KeywordValueActor]())

    // Simulate an ext start obs arriving
    observationHandler.onObservationEvent(ObservationEvent.EXT_START_OBS, dataLabel)

    for {evt <- ObservationEvent.values()
         if (evt != ObservationEvent.EXT_END_OBS && evt != ObservationEvent.EXT_START_OBS)} {
      observationHandler.onObservationEvent(evt, dataLabel)
    }
    sleep(500)
    verify(mockPublisher).sendData(GDSStartObservation(dataLabel))
//    verify(registrar).startObservation(dataLabel)
    sleep(500)
    verify(mockPublisher, times(0)).sendData(GDSEndObservation(dataLabel))
//    verify(registrar, times(0)).endObservation(dataLabel)

    // Simulate an end obs arriving
    observationHandler.onObservationEvent(ObservationEvent.EXT_END_OBS, dataLabel)
    sleep(500)
    verify(mockPublisher).sendData(GDSEndObservation(dataLabel))
//    verify(registrar).endObservation(dataLabel)

  }

  private def sleep(time: Long) {
    TimeUnit.MILLISECONDS.sleep(time)
  }

  after {
    dummyFile.delete()
  }
}