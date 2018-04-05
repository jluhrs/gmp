package edu.gemini.aspen.gds.obsevent.handler

import edu.gemini.aspen.gds.actors._
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}
import edu.gemini.aspen.gmp.services.PropertyHolder
import org.osgi.service.event.{Event, EventAdmin, EventHandler}

/**
 * Simple Observation Event Handler that creates a KeywordSetComposer and launches the
 * keyword values acquisition process
 */
class GDSObseventHandlerImpl(actorsFactory: CompositeActorsFactory, keywordsDatabase: KeywordsDatabase, postProcessingPolicy: CompositePostProcessingPolicy, propertyHolder: PropertyHolder, ea: EventAdmin) extends GDSObseventHandler with EventHandler {

  private val replyHandler = new ReplyHandler(actorsFactory, keywordsDatabase, postProcessingPolicy, propertyHolder, ea)

  override def handleEvent(event: Event): Unit = {
    event.getProperty(GDSObseventHandler.ObsEventKey) match {
      case (e: ObservationEvent, d: DataLabel) => replyHandler ! AcquisitionRequest(e, d)
      case _ => sys.error("Uknown message from observation event")
    }
  }
}