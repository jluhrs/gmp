package edu.gemini.aspen.gds.seqexec


import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Retrieve
import edu.gemini.aspen.gds.api._

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class SeqexecActor(seqexecKeyDB: TemporarySeqexecKeywordsDatabase, dataLabel: DataLabel, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val s = System.currentTimeMillis()
    val seqexecValue = (seqexecKeyDB !? Retrieve(dataLabel, fitsKeyword)).asInstanceOf[Option[Any]]
    LOG.fine("Retrieving SEQEXEC keyword " + fitsKeyword + " took " + (System.currentTimeMillis() - s) + "[ms]")

    seqexecValue map (valueToCollectedValue) orElse (defaultCollectedValue) toList
  }
}
