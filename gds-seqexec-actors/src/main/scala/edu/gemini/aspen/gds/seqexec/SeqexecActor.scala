package edu.gemini.aspen.gds.seqexec


import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.RetrieveAll
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.Conversions._
import fits.FitsKeyword
import java.util.logging.Logger

/**
 * Actor that collects all requested seqexec keywords at once
 */
class SeqexecActor(seqexecKeyDB: TemporarySeqexecKeywordsDatabase, dataLabel: DataLabel, configurations: List[GDSConfiguration]) extends KeywordValueActor {
  protected val LOG = Logger.getLogger(this.getClass.getName)
  protected var seqexecValuesMap: Map[FitsKeyword, AnyRef] = _

  def collectValues(): List[CollectedValue[_]] = {
    val s = System.currentTimeMillis()
    seqexecValuesMap = (seqexecKeyDB !? RetrieveAll(dataLabel)).asInstanceOf[Map[FitsKeyword, AnyRef]]
    //todo: after retrieving we should clean up the database
    //seqexecKeyDB !? Clean(dataLabel)
    LOG.fine("Retrieving All SEQEXEC keywords took " + (System.currentTimeMillis() - s) + "[ms]")

    (for {config <- configurations} yield {
      new OneItemSeqexecValueActor(config, seqexecValuesMap).collectValues().headOption
    }) collect {
      case x: Some[CollectedValue[_]] => x.get
    }
  }

  private class OneItemSeqexecValueActor(config: GDSConfiguration, map: Map[FitsKeyword, AnyRef]) extends OneItemKeywordValueActor(config) {
    def collectValues(): List[CollectedValue[_]] = {
      seqexecValuesMap.get(config.keyword).map {
        valueToCollectedValue
      }.toList
    }
  }

}