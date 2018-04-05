package edu.gemini.aspen.gds.actors.factory

import org.junit.runner.RunWith
import org.junit.Assert._
import org.scalatest.junit.JUnitRunner
import org.mockito.Mockito._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService
import org.scalatest.FunSuite
import org.scalatest.mock.MockitoSugar
import edu.gemini.aspen.gds.actors.{ConfigurableActorsFactory, DummyActorsFactory}
import edu.gemini.aspen.gds.api.GDSConfiguration
import edu.gemini.aspen.gds.api.Conversions._

@RunWith(classOf[JUnitRunner])
class CompositeActorsFactoryImplTest extends FunSuite with MockitoSugar {
  val dataLabel = new DataLabel("GS-2011")

  test("should return an empty list of Actors when no Factories are registered") {
    val startObservationFactory = new CompositeActorsFactoryImpl(mock[GDSConfigurationService])
    val actors = startObservationFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)

    assertTrue(actors.isEmpty)
  }

  test("should return a non empty list of actors for buildStartAcquisitionActors") {
    val startObservationFactory = new CompositeActorsFactoryImpl(mock[GDSConfigurationService])

    // Register dummy factory
    startObservationFactory.addFactory(new DummyActorsFactory())

    val actors = startObservationFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(1, actors.size)
  }

  test("should return a non empty list of actors for buildPrepareObservationActors") {
    val startObservationFactory = new CompositeActorsFactoryImpl(mock[GDSConfigurationService])

    // Register dummy factory
    startObservationFactory.addFactory(new DummyActorsFactory())

    val actors = startObservationFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
    assertEquals(1, actors.size)
  }

  test("should return a non empty list of actors for buildEndAcquisitionActors") {
    val startObservationFactory = new CompositeActorsFactoryImpl(mock[GDSConfigurationService])

    // Register dummy factory
    startObservationFactory.addFactory(new DummyActorsFactory())

    val actors = startObservationFactory.buildActors(ObservationEvent.OBS_END_ACQ, dataLabel)
    assertEquals(1, actors.size)
  }

  test("should return a non empty list of actors after registration and unregistration of a factory") {
    val startObservationFactory = new CompositeActorsFactoryImpl(mock[GDSConfigurationService])

    // Register dummy factory
    val actorsFactory = new DummyActorsFactory()
    startObservationFactory.addFactory(actorsFactory)

    // Unregister dummy factory
    startObservationFactory.removeFactory(actorsFactory)

    val actors = startObservationFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertTrue(actors.isEmpty)
  }

  test("configuration updates should reflect changes right away, bug GIAPI-922") {
    val service = mock[GDSConfigurationService]
    when(service.getConfiguration).thenReturn(Nil)
    val startObservationFactory = new CompositeActorsFactoryImpl(service)

    // Register configurable factory
    val actorsFactory = new ConfigurableActorsFactory()
    startObservationFactory.addFactory(actorsFactory)

    val actors = startObservationFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertTrue(actors.isEmpty)

    // Now GDSConfigurationService has one configuration item
    reset(service)
    when(service.getConfiguration).thenReturn(new GDSConfiguration("GPI", "OBS_START_ACQ", "KEY", 0, "INT", true, "null", "SEQEXEC", "KEY", 0, "", "my comment") :: Nil)

    // Build actors shouldn't need to be restarted
    val newActors = startObservationFactory.buildActors(ObservationEvent.OBS_START_ACQ, dataLabel)
    assertEquals(1, newActors.size)

  }

}