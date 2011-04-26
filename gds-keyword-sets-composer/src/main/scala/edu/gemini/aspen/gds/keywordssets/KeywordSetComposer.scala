package edu.gemini.aspen.gds.keywordssets

import java.util.logging.Logger
import actors.{OutputChannel, Actor}
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase

sealed abstract class AcquisitionRequest

/**
 * Message to indicate that a new observation was initiated
 */
case class StartAcquisition(dataLabel: DataLabel) extends AcquisitionRequest

/**
 * Message to indicate that an observation was completed
 */
case class EndAcquisition(dataLabel: DataLabel) extends AcquisitionRequest

sealed abstract class AcquisitionReply

/**
 * Message to indicate that the data collection was completed
 * It is sent in reply to an StartAcquisition message
 */
case class StartAcquisitionReply(dataLabel: DataLabel) extends AcquisitionReply

/**
 * An actor that can compose data items from a set of independent actors
 */
class KeywordSetComposer(actorsFactory: KeywordActorsFactory, keywordsDatabase: KeywordsDatabase) extends Actor {
    val LOG = KeywordSetComposer.LOG

    // Start automatically
    start

    def act() {
        loop {
            react {
                case StartAcquisition(dataLabel) => startKeywordCollection(sender, dataLabel)
                case EndAcquisition(dataLabel) => finishKeywordSetCollection(dataLabel)
                case _ => throw new RuntimeException("Argument not known ")
            }
        }
    }

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
            LOG.info("All collecting actors completed.")
            // Reply to the original sender
            sender ! StartAcquisitionReply(dataLabel)
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

    def apply(actorsFactory: KeywordActorsFactory, keywordsDatabase: KeywordsDatabase) = new KeywordSetComposer(actorsFactory, keywordsDatabase)
}