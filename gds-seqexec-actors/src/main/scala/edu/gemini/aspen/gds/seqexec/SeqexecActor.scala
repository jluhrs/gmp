package edu.gemini.aspen.gds.seqexec


import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.staticheaderreceiver.{Retrieve, TemporarySeqexecKeywordsDatabase}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class SeqexecActor(seqexecKeyDB: TemporarySeqexecKeywordsDatabase, dataLabel: DataLabel, configuration: GDSConfiguration) extends KeywordValueActor {

  override def collectValues(): List[CollectedValue] = {

    val (channelName, fitsKeyword, fitsComment, headerIndex) = (
      configuration.channel.name,
      configuration.keyword,
      configuration.fitsComment.value,
      configuration.index.index
      )

    val value = (seqexecKeyDB.channel !? Retrieve(dataLabel, fitsKeyword)).asInstanceOf[Option[AnyRef]]

    value map {
      v => CollectedValue(fitsKeyword, v, fitsComment, headerIndex)
    } toList

  }

}
