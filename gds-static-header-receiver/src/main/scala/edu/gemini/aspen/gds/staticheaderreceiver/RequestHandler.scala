package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}
import actors.Actor

object RequestHandler extends Actor {
  private var keywordsDatabase: TemporarySeqexecKeywordsDatabase = _

  def setDatabase(keywordsDatabase: TemporarySeqexecKeywordsDatabase) {
    this.keywordsDatabase = keywordsDatabase
  }


  def act() {
    loop {
      react {
        case InitObservation(programId, dataLabel) => initObservation(programId, dataLabel)
        case StoreKeyword(dataLabel, keyword, value) => storeKeyword(dataLabel, keyword, value)
        case x: Any => throw new RuntimeException("Argument not known: " + x)
      }
    }
  }

  def initObservation(programId: String, dataLabel: DataLabel) {
    println("Program ID: "+programId+" Data label: "+dataLabel)
    //send this information to somebody
  }

  def storeKeyword(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) {
    println("Data label: "+dataLabel+" Keyword: "+keyword+" Value: "+value)
    keywordsDatabase ! StoreKeyword(dataLabel, keyword, value)
  }
}