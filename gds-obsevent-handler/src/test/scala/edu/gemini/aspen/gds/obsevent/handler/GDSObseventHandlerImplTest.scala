package edu.gemini.aspen.gds.obsevent.handler

import org.mockito.Mockito._
import org.mockito.Matchers.anyString
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.keywords.database.impl.KeywordsDatabaseImpl
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.api.{CompositeErrorPolicyImpl, KeywordValueActor}
import org.junit.Test
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar
import java.io.File
import edu.gemini.aspen.gmp.services.PropertyHolder

class GDSObseventHandlerImplTest {
  val actorsFactory = mock(classOf[CompositeActorsFactory])
  val propertyHolder = mock(classOf[PropertyHolder])
  val keywordsDatabase = new KeywordsDatabaseImpl()
  val tempDir = System.getProperty("java.io.tmpdir")
  when(propertyHolder.getProperty(anyString())).thenReturn(tempDir)

  val registrar = mock(classOf[ObservationStateRegistrar])
  val dataLabel = new DataLabel("GS-2011")
  private val observationHandler = new GDSObseventHandlerImpl(actorsFactory, keywordsDatabase, new CompositeErrorPolicyImpl(), registrar, propertyHolder)

  @Test
  def testGDSObseventHandler {
    new File(tempDir, dataLabel.getName + ".fits").createNewFile()

    for (evt <- ObservationEvent.values()) {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

      observationHandler.onObservationEvent(evt, dataLabel)

      Thread.sleep(300)

      // verify mock
      verify(actorsFactory).buildActors(evt, dataLabel)
    }
    verify(registrar).startObservation(dataLabel)
    Thread.sleep(1500)
    verify(registrar).endObservation(dataLabel)
  }


  @Test
  def testWithMissingEvent {
    new File(tempDir, dataLabel.getName + ".fits").createNewFile()

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

  @Test
  def testWithSlowEvent {
    new File(tempDir, dataLabel.getName + ".fits").createNewFile()

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
    Thread.sleep(1500)
    observationHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel)
    Thread.sleep(1500)
    verify(registrar).endObservation(dataLabel)
  }
}