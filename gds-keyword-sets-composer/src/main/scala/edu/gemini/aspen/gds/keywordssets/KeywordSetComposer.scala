package edu.gemini.aspen.gds.keywordssets

import edu.gemini.aspen.giapi.data.Dataset
import actors.Actor
import java.util.logging.Logger

/**
 * Message to indicate that a new observation was initiated
 */
case class Init(dataSet: Dataset)

/**
 * Message to indicate that an observation was completed
 */
case class Complete(dataSet: Dataset)

class KeywordSetComposer extends Actor {
    val LOG = KeywordSetComposer.LOG

    start
    
    def act() = {
        loop {
            react {
                case Init(dataSet) => initKeywordSetCollection(dataSet)
                case Complete(dataSet) => finishKeywordSetCollection(dataSet)
                case _ => throw new RuntimeException("Argument not known")
            }
        }
    }

    private def initKeywordSetCollection(dataSet: Dataset) = {
        LOG.info("Init keyword collection on dataset " + dataSet)
    }

    private def finishKeywordSetCollection(dataSet: Dataset) = {
        LOG.info("Complete keyword collection on dataset " + dataSet)
    }
}

object KeywordSetComposer {
    private val LOG = Logger.getLogger(classOf[KeywordSetComposer].getName)

    def apply() = new KeywordSetComposer
}