package edu.gemini.aspen.gds.obsevent.handler

import org.mockito.Mockito._
import org.mockito.Matchers.anyString
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.keywords.database.impl.KeywordsDatabaseImpl
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.api.{CompositeErrorPolicyImpl, KeywordValueActor}
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import java.io.File
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfter, OneInstancePerTest, FunSuite}

@RunWith(classOf[JUnitRunner])
class GDSObseventHandlerImplTest extends FunSuite with OneInstancePerTest with BeforeAndAfter {
  val actorsFactory = mock(classOf[CompositeActorsFactory])
  val propertyHolder = mock(classOf[PropertyHolder])
  val keywordsDatabase = new KeywordsDatabaseImpl()
  val tempDir = System.getProperty("java.io.tmpdir")
  when(propertyHolder.getProperty(anyString())).thenReturn(tempDir)

  val registrar = mock(classOf[ObservationStateRegistrar])
  val dataLabel = new DataLabel("GS-2011.fits")
  val dummyFile = new File(tempDir, dataLabel.getName)

  before {
    dummyFile.createNewFile()
  }

  private val observationHandler = new GDSObseventHandlerImpl(actorsFactory, keywordsDatabase, new CompositeErrorPolicyImpl(), registrar, propertyHolder)

  test("verify all events") {

    for (evt <- ObservationEvent.values()) {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

      observationHandler.onObservationEvent(evt, dataLabel)

      Thread.sleep(300)

      // verify mock
      verify(actorsFactory).buildActors(evt, dataLabel)
    }
    verify(registrar).startObservation(dataLabel)
    Thread.sleep(500)
    verify(registrar).endObservation(dataLabel)
  }

  test("with a missing event") {
    for (evt <- ObservationEvent.values()) {
      if (evt != ObservationEvent.OBS_START_ACQ) {
        when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

        observationHandler.onObservationEvent(evt, dataLabel)

        Thread.sleep(300)

        // verify mock
        verify(actorsFactory).buildActors(evt, dataLabel)
      }
    }
    verify(registrar).startObservation(dataLabel)
    verify(registrar, times(0)).endObservation(dataLabel)
    Thread.sleep(5500)
    verify(registrar).endObservation(dataLabel)
  }

  test("with slow event") {
    for (evt <- ObservationEvent.values()) {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

      if (evt != ObservationEvent.OBS_START_ACQ) {
        observationHandler.onObservationEvent(evt, dataLabel)

        Thread.sleep(300)

        // verify mock
        verify(actorsFactory).buildActors(evt, dataLabel)
      }
    }
    verify(registrar).startObservation(dataLabel)
    verify(registrar, times(0)).endObservation(dataLabel)
    Thread.sleep(500)
    observationHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel)
    Thread.sleep(1500)
    verify(registrar).endObservation(dataLabel)
  }

  test("without start/end transaction") {
    for {evt <- ObservationEvent.values()
      if (evt != ObservationEvent.EXT_END_OBS && evt != ObservationEvent.EXT_START_OBS)
    } {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

        observationHandler.onObservationEvent(evt, dataLabel)

        Thread.sleep(100)

        // verify mock
        verify(actorsFactory).buildActors(evt, dataLabel)
      }
    verify(registrar).startObservation(dataLabel)
    Thread.sleep(500)
    verify(registrar).endObservation(dataLabel)
  }

  test("with start transaction but no end transaction") {
    // Simulate an ext start obs arriving
    when(actorsFactory.buildActors(ObservationEvent.EXT_START_OBS, dataLabel)).thenReturn(List[KeywordValueActor]())

    observationHandler.onObservationEvent(ObservationEvent.EXT_START_OBS, dataLabel)

    for {evt <- ObservationEvent.values()
         if (evt != ObservationEvent.EXT_END_OBS && evt != ObservationEvent.EXT_START_OBS)}
    {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

      observationHandler.onObservationEvent(evt, dataLabel)

      Thread.sleep(100)

      // verify mock
      verify(actorsFactory).buildActors(evt, dataLabel)
    }
    verify(registrar).startObservation(dataLabel)
    Thread.sleep(500)
    verify(registrar, times(0)).endObservation(dataLabel)
  }

  after {
    dummyFile.delete()
  }
}