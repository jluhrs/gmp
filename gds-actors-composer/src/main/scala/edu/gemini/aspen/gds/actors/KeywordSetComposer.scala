package edu.gemini.aspen.gds.actors

import java.util.logging.Logger
import actors.{OutputChannel, Actor}
import edu.gemini.aspen.gds.keywords.database.{Store, KeywordsDatabase}
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

/**
 * Message to indicate that FITS header data collection should begin
 */
case class AcquisitionRequest(obsEvent: ObservationEvent, dataLabel: DataLabel)

/**
 * Message to indicate the data collection was completed
 * It is sent in reply to an AcquisitionRequest message
 */
case class AcquisitionRequestReply(obsEvent: ObservationEvent, dataLabel: DataLabel)

/**
 * An actor that composes the items required to complete an observation using a set of actors
 */
class KeywordSetComposer(actorsFactory: KeywordActorsFactory, keywordsDatabase: KeywordsDatabase) extends Actor {
  val LOG = KeywordSetComposer.LOG

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

    val dataFutures = requestCollection(obsEvent, dataLabel, actorsFactory.buildActors)

    waitForDataAndReply(dataLabel, dataFutures) {
      LOG.info("All collecting actors completed.")
      // Reply to the original sender
      sender ! AcquisitionRequestReply(obsEvent, dataLabel)
    }
  }

  private def requestCollection(obsEvent: ObservationEvent, dataLabel: DataLabel, actorsBuilder: (ObservationEvent, DataLabel) => List[Actor]) = {
    // Get the actors from the factory
    val actors = actorsBuilder(obsEvent, dataLabel)

    // Start collecting
    val dataFutures = for (dataActor <- actors) yield {
      dataActor !! Collect
    }
    dataFutures
  }

  private def waitForDataAndReply(dataLabel: DataLabel, dataFutures: List[Future[Any]])(postAction: => Unit) {
    // Wait for response
    var i = 0
    loopWhile(i < dataFutures.size) {
      i += 1
      dataFutures(i - 1).inputChannel.react {
        case data => storeReply(dataLabel, data)
      }
    } andThen {
      postAction
    }
  }

  private def storeReply(dataLabel: DataLabel, collectedValues: Any) {
    for (value <- collectedValues.asInstanceOf[List[CollectedValue[_]]]) {
      keywordsDatabase ! Store(dataLabel, value)
    }
  }

}

/**
 * Companion object providing factory methods
 */
object KeywordSetComposer {
  private val LOG = Logger.getLogger(classOf[KeywordSetComposer].getName)

  def apply(actorsFactory: KeywordActorsFactory, keywordsDatabase: KeywordsDatabase) = new KeywordSetComposer(actorsFactory, keywordsDatabase)
}