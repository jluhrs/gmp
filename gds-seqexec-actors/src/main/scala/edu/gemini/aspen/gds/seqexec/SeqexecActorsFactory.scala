package edu.gemini.aspen.gds.seqexec


import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class SeqexecActorsFactory(@Requires seqexecKeyDB: TemporarySeqexecKeywordsDatabase) extends KeywordActorsFactory {
  var conf: List[GDSConfiguration] = List()

  override def buildPrepareObservationActors(dataLabel: DataLabel) = {
    conf filter {
      _.event.name == ObservationEvent.OBS_PREP.toString
    } map {
      case config: GDSConfiguration => new SeqexecActor(seqexecKeyDB, dataLabel, config)
    }
  }

  override def configure(configuration: List[GDSConfiguration]) {
    conf = configuration filter {
      _.subsystem.name == "SEQEXEC"
    }
  }

}