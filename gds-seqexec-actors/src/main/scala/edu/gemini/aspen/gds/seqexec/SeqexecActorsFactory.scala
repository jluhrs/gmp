package edu.gemini.aspen.gds.seqexec


import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.gds.api.{AbstractKeywordActorsFactory, KeywordSource, KeywordActorsFactory}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class SeqexecActorsFactory(@Requires seqexecKeyDB: TemporarySeqexecKeywordsDatabase) extends AbstractKeywordActorsFactory {

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