package edu.gemini.aspen.gds.keywordssets

import edu.gemini.aspen.giapi.data.Dataset
import java.util.logging.Logger
import actors.{OutputChannel, Actor}

/**
 * Message to indicate that a new observation was initiated
 */
case class Init(dataSet: Dataset)

/**
 * Message to indicate that the data collection was completed
 * It is sent in reply to an Init message
 */
case class InitCompleted(dataSet: Dataset)

/**
 * Message to indicate that an observation was completed
 */
case class Complete(dataSet: Dataset)

/**
 * An actor that can compose data items from a set of independent actors
 */
class KeywordSetComposer(actorsFactory: KeywordActorsFactory) extends Actor {
    val LOG = KeywordSetComposer.LOG

    // Start automatically
    start

    def act() {
        loop {
            react {
                case Init(dataSet) => startKeywordCollection(sender, dataSet)
                case Complete(dataSet) => finishKeywordSetCollection(dataSet)
                case _ => throw new RuntimeException("Argument not known ")
            }
        }
    }

    def observationInit(dataSet: Dataset) = this ! Init(dataSet)

    def observationComplete(dataSet: Dataset) = this ! Init(dataSet)

    private def startKeywordCollection(sender: OutputChannel[Any], dataSet: Dataset) {
        LOG.info("Init keyword collection on dataset " + dataSet)
        // Get the actors from the factory
        val actors = actorsFactory.startObservationActors(dataSet)
        // Start collecting
        val dataFutures = for (dataActor <- actors) yield {
            dataActor !! Collect
        }
        // Wait for response
        var i = 0
        loopWhile(i < actors.size) {
            i += 1
            dataFutures(i - 1).inputChannel.react {
                case data => storeReply(data)
            }
        } andThen {
            // Reply to the original sender
            sender ! InitCompleted(dataSet)
            LOG.info("OK, all collecting actors completed.")
        }
    }

    private def storeReply(data: Any) {
        // TODO: store the data in the database
        println(data)
    }

    private def finishKeywordSetCollection(dataSet: Dataset) {
        LOG.info("Complete keyword collection on dataset " + dataSet)
    }
}

/**
 * Companion object providing factory methods
 */
object KeywordSetComposer {
    private val LOG = Logger.getLogger(classOf[KeywordSetComposer].getName)

    def apply(actorsFactory: KeywordActorsFactory) = new KeywordSetComposer(actorsFactory)
}