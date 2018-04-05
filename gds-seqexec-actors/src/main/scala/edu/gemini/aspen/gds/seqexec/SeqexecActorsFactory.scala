package edu.gemini.aspen.gds.seqexec

import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}

class SeqexecActorsFactory(seqexecKeyDB: TemporarySeqexecKeywordsDatabase) extends AbstractKeywordActorsFactory {

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    new SeqexecActor(
      seqexecKeyDB,
      dataLabel,
      actorsConfiguration filter {
        _.event.name == obsEvent.name()
      }) :: Nil
  }

  override def getSource = KeywordSource.SEQEXEC

}