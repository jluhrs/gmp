package edu.gemini.aspen.gds.seqexec


import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabase
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.Retrieve
import edu.gemini.aspen.gds.api.{DataType, KeywordValueActor, GDSConfiguration, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class SeqexecActor(seqexecKeyDB: TemporarySeqexecKeywordsDatabase, dataLabel: DataLabel, configuration: GDSConfiguration) extends KeywordValueActor {

  override def collectValues(): List[CollectedValue[_]] = {

    val (channelName, fitsKeyword, fitsComment, headerIndex, dataType) = (
      configuration.channel.name,
      configuration.keyword,
      configuration.fitsComment.value,
      configuration.index.index,
      configuration.dataType
      )
    dataType match {
      case DataType("STRING") => (seqexecKeyDB !? Retrieve(dataLabel, fitsKeyword)).asInstanceOf[Option[String]] map {
        v => CollectedValue(fitsKeyword, v, fitsComment, headerIndex)
      } toList
      case DataType("DOUBLE") => (seqexecKeyDB !? Retrieve(dataLabel, fitsKeyword)).asInstanceOf[Option[Double]] map {
        v => CollectedValue(fitsKeyword, v, fitsComment, headerIndex)
      } toList
      case DataType("INT") => (seqexecKeyDB !? Retrieve(dataLabel, fitsKeyword)).asInstanceOf[Option[Int]] map {
        v => CollectedValue(fitsKeyword, v, fitsComment, headerIndex)
      } toList
    }


  }

}
