package edu.gemini.aspen.gds.actors

import java.util.logging.Logger
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import scala.actors.{Futures, OutputChannel, Actor}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.gds.keywords.database.{StoreList, KeywordsDatabase}
import org.joda.time.DateTime
import scala._

/**
 * Message to indicate that FITS header data collection should begin
 */
case class AcquisitionRequest(obsEvent: ObservationEvent, dataLabel: DataLabel)

/**
 * Message to indicate the data collection w  as completed
 * It is sent in reply to an AcquisitionRequest message
 */
case class AcquisitionRequestReply(obsEvent: ObservationEvent, dataLabel: DataLabel)

/**
 * An actor that composes the items required to complete an observation using a set of actors
 */
class KeywordSetComposer(actorsFactory: KeywordActorsFactory, keywordsDatabase: KeywordsDatabase) extends Actor {
  private val LOG = Logger.getLogger(this.getClass.getName)

  // Start automatically
  start()

  def act() {
    react {
      case AcquisitionRequest(obsEvent, dataLabel) => doCollection(sender, obsEvent, dataLabel)
      case _ => error("Unknown request type ")
    }
  }

  private def doCollection(sender: OutputChannel[Any], obsEvent: ObservationEvent, dataLabel: DataLabel) {
    LOG.info("Keyword collection on dataset " + dataLabel + " for event " + obsEvent.name)

    val s = System.currentTimeMillis()
    val dataFutures = requestCollection(obsEvent, dataLabel, actorsFactory.buildActors)

    waitForDataAndReply(dataLabel, dataFutures) {
      LOG.info(dataFutures.size + " collecting actors for " + obsEvent + " completed in " + (System.currentTimeMillis() - s) + " [ms]")
      // Reply to the original sender
      sender ! AcquisitionRequestReply(obsEvent, dataLabel)
    }
  }

  private def requestCollection(obsEvent: ObservationEvent, dataLabel: DataLabel, actorsBuilder: (ObservationEvent, DataLabel) => List[Actor]) = {
    // Get the actors from the factory
    val actors = measureDuration("Building actors for event:" + obsEvent) {
      actorsBuilder(obsEvent, dataLabel)
    }

    // Start collecting
    val dataFutures = measureDuration("Sending collection request for " + obsEvent) {
      for (dataActor <- actors) yield
        dataActor !! Collect
    }

    dataFutures
  }

  private def waitForDataAndReply(dataLabel: DataLabel, dataFutures: List[Future[Any]])(postAction: => Unit) {
    measureDuration("Waiting for " + dataFutures.size + " data items ") {
      // Wait for response
      val v = Futures awaitAll (500, dataFutures: _*) collect {
        case Some(x: List[CollectedValue[_]]) => x
        case x: Any => error("Cannot be " + x)
      }

      keywordsDatabase ! StoreList(dataLabel, v.flatten)
    }

    postAction
  }

  private def measureDuration[T](msg: String)(action: => T): T = {
    val s = new DateTime()
    val r = action
    val e = new DateTime()
    LOG.fine(msg + " took " + (s to e).toDuration + " [ms]")
    r
  }

}