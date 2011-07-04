package edu.gemini.aspen.gds.obsevent.handler

import org.mockito.Mockito._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabaseImpl
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.api.{CompositeErrorPolicyImpl, KeywordValueActor}
import org.junit.Test
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar


class GDSObseventHandlerTest {
    val actorsFactory = mock(classOf[CompositeActorsFactory])
    val keywordsDatabase = new KeywordsDatabaseImpl()

    private val observationHandler = new GDSObseventHandler(actorsFactory, keywordsDatabase, new CompositeErrorPolicyImpl(), mock(classOf[ObservationStateRegistrar]))

    @Test
    def testGDSObseventHandler {
        val dataLabel = new DataLabel("GS-2011")

        for (evt <- ObservationEvent.values()) {
            when(actorsFactory.buildActors(evt, dataLabel)).thenReturn(List[KeywordValueActor]())

            observationHandler.onObservationEvent(evt, dataLabel)

            Thread.sleep(300)

            // verify mock
            verify(actorsFactory).buildActors(evt, dataLabel)
        }
    }
}