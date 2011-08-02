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

class GDSObseventHandlerTest {
  val actorsFactory = mock(classOf[CompositeActorsFactory])
  val propertyHolder = mock(classOf[PropertyHolder])
  val keywordsDatabase = new KeywordsDatabaseImpl()
  val tempDir = System.getProperty("java.io.tmpdir")

  private val observationHandler = new GDSObseventHandler(actorsFactory, keywordsDatabase, new CompositeErrorPolicyImpl(), mock(classOf[ObservationStateRegistrar]), propertyHolder)

  @Test
  def testGDSObseventHandler {
    val dataLabel = new DataLabel("GS-2011")
    when(propertyHolder.getProperty(anyString())).thenReturn(tempDir)

    new File(tempDir, dataLabel.getName + ".fits").createNewFile()

    for (evt <- ObservationEvent.values()) {
      when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

      observationHandler.onObservationEvent(evt, dataLabel)

      Thread.sleep(300)

      // verify mock
      verify(actorsFactory).buildActors(evt, dataLabel)
    }
  }
}