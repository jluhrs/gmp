package edu.gemini.aspen.gds.keywordssets

import edu.gemini.aspen.giapi.data.Dataset
import actors.Actor
import java.util.logging.Logger


case class Init(dataSet: Dataset)

case class Complete(dataSet: Dataset)

class KeywordSetComposer extends Actor {
    val LOG = KeywordSetComposer.LOG
    def act() = {
        loop {
            react {
                case Init(dataSet) => initKeywordSetCollection(dataSet)
            }
        }
    }

    def initKeywordSetCollection(dataSet: Dataset) {
        LOG.info("Init keyword collection on dataset " + dataSet)
    }
}

object KeywordSetComposer {
    private val LOG = Logger.getLogger(classOf[KeywordSetComposer].getName)

    def apply() = new KeywordSetComposer
}