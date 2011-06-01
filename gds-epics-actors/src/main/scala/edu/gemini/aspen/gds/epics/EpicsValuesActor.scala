package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends KeywordValueActor {
  override def collectValues(): List[CollectedValue[_]] = {

    val (channelName, fitsKeyword, fitsComment, headerIndex) = (
      configuration.channel.name,
      configuration.keyword,
      configuration.fitsComment.value,
      configuration.index.index
      )

    val epicsValue = epicsReader.getValue(channelName)

    //todo: add support for other data types
    if (epicsValue.isInstanceOf[Array[Double]]) {
      // TODO: This should be done on the EpicsArray Actor
      CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](0), fitsComment, headerIndex) :: Nil
    } else if (epicsValue.isInstanceOf[String]) {
      // TODO: This should be done on the EpicsArray Actor
      CollectedValue(fitsKeyword, epicsValue.asInstanceOf[String], fitsComment, headerIndex) :: Nil
    } else {
      Nil
    }
  }

}
