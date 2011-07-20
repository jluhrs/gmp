package edu.gemini.aspen.gds.seqexec


import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.gds.api.{KeywordSource, KeywordActorsFactory}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class SeqexecActorsFactory(@Requires seqexecKeyDB: TemporarySeqexecKeywordsDatabase) extends KeywordActorsFactory {

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel) = {
    actorsConfiguration filter {
      _.event.name == obsEvent.name()
    } map {
      c => new SeqexecActor(seqexecKeyDB, dataLabel, c)
    }
  }

  override def getSource = KeywordSource.SEQEXEC

}