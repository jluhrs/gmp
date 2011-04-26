package edu.gemini.aspen.gds.keywordssets

import java.util.logging.Logger
import actors.{OutputChannel, Actor}
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}

/**
 * Message to indicate that a new observation was initiated
 */
case class Init(dataLabel: DataLabel)

/**
 * Message to indicate that the data collection was completed
 * It is sent in reply to an Init message
 */
case class InitCompleted(dataLabel: DataLabel)

/**
 * Message to indicate that an observation was completed
 */
case class Complete(dataLabel: DataLabel)

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
                case Init(dataLabel) => startKeywordCollection(sender, dataLabel)
                case Complete(dataLabel) => finishKeywordSetCollection(dataLabel)
                case _ => throw new RuntimeException("Argument not known ")
            }
        }
    }

    def observationInit(dataLabel: DataLabel) = this ! Init(dataLabel)

    def observationComplete(dataLabel: DataLabel) = this ! Init(dataLabel)

    private def startKeywordCollection(sender: OutputChannel[Any], dataLabel: DataLabel) {
        LOG.info("Init keyword collection on dataset " + dataLabel)
        // Get the actors from the factory
        val actors = actorsFactory.startObservationActors(dataLabel)
        
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
            sender ! InitCompleted(dataLabel)
            LOG.info("OK, all collecting actors completed.")
        }
    }

    private def storeReply(collectedValue:Any) {
        println(collectedValue)
    }

    private def finishKeywordSetCollection(dataLabel: DataLabel) {
        LOG.info("Complete keyword collection on dataset " + dataLabel)
    }
}

/**
 * Companion object providing factory methods
 */
object KeywordSetComposer {
    private val LOG = Logger.getLogger(classOf[KeywordSetComposer].getName)

    def apply(actorsFactory: KeywordActorsFactory) = new KeywordSetComposer(actorsFactory)
}