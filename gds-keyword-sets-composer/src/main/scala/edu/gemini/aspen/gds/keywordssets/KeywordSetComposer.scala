package edu.gemini.aspen.gds.keywordssets

import java.util.logging.Logger
import actors.{OutputChannel, Actor}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.actors.{Collect, KeywordActorsFactory}
import edu.gemini.aspen.gds.keywords.database.{Store, KeywordsDatabase}
import edu.gemini.aspen.gds.api.CollectedValue
import edu.gemini.aspen.gds.api.Conversions._

/**
 * Parent class of request to KeywordSetComposer
 */
sealed abstract class AcquisitionRequest

/**
 * Message to indicate that a new observation was initiated
 */
case class StartAcquisition(dataLabel: DataLabel) extends AcquisitionRequest

/**
 * Message to indicate that an observation was completed
 */
case class EndAcquisition(dataLabel: DataLabel) extends AcquisitionRequest

/**
 * Parent class of actor's replies to KeywordSetComposer
 */
sealed abstract class AcquisitionReply

/**
 * Message to indicate that the data collection was completed
 * It is sent in reply to an StartAcquisition message
 */
case class StartAcquisitionReply(dataLabel: DataLabel) extends AcquisitionReply

/**
 * Message to indicate that the data collection was completed
 * It is sent in reply to an StartAcquisition message
 */
case class EndAcquisitionReply(dataLabel: DataLabel) extends AcquisitionReply

/**
 * An actor that composes the items required to complete an observation using a set of actors
 */
class KeywordSetComposer(actorsFactory: KeywordActorsFactory, keywordsDatabase: KeywordsDatabase) extends Actor {
    val LOG = KeywordSetComposer.LOG

    // Start automatically
    start

    def act() {
        loop {
            react {
                case StartAcquisition(dataLabel) => startKeywordCollection(sender, dataLabel)
                case EndAcquisition(dataLabel) => finishKeywordSetCollection(sender, dataLabel)
                case x:Any => throw new RuntimeException("Argument not known " + x)
            }
        }
    }

    private def startKeywordCollection(sender: OutputChannel[Any], dataLabel: DataLabel) {
        LOG.info("Init keyword collection on dataset " + dataLabel)

        val dataFutures = requestCollection(dataLabel, actorsFactory.startAcquisitionActors)

        waitForDataAndReply(dataLabel, dataFutures) {
            LOG.info("All collecting actors completed.")
            // Reply to the original sender
            sender ! StartAcquisitionReply(dataLabel)
        }
    }

    private def requestCollection(dataLabel: DataLabel, actorsBuilder: (DataLabel) => List[Actor]) = {
        // Get the actors from the factory
        val actors = actorsBuilder(dataLabel)

        // Start collecting
        val dataFutures = for (dataActor <- actors) yield {
            dataActor !! Collect
        }
        dataFutures
    }

    private def waitForDataAndReply(dataLabel:DataLabel, dataFutures: List[Future[Any]])(postAction: => Unit) {
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
        println(collectedValues)
        for (value <- collectedValues.asInstanceOf[List[CollectedValue]])
            keywordsDatabase ! Store(dataLabel, value)
    }

    private def finishKeywordSetCollection(sender: OutputChannel[Any], dataLabel: DataLabel) {
        LOG.info("Complete keyword collection on dataset " + dataLabel)
        val dataFutures = requestCollection(dataLabel, actorsFactory.endAcquisitionActors)

        waitForDataAndReply(dataLabel, dataFutures) {
            LOG.info("All collecting actors completed.")
            // Reply to the original sender
            sender ! EndAcquisitionReply(dataLabel)
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