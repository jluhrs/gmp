package edu.gemini.aspen.gds.keywordssets

import edu.gemini.aspen.giapi.data.Dataset
import actors.Actor

case class Init(dataSet:Dataset)

case class Complete(dataSet:Dataset)

class KeywordSetComposer extends Actor {
    def act() {}
}

object KeywordSetComposer