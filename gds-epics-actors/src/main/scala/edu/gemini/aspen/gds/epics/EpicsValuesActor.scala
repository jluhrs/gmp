package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, config: GDSConfiguration) extends KeywordValueActor {
    override def collectValues(): List[CollectedValue[_]] = {
        val (channelName, fitsKeyword, fitsComment, headerIndex) = (
                config.channel.name,
                config.keyword,
                config.fitsComment.value,
                config.index.index)

        val readValue = Option(epicsReader.getValue(channelName))

        if (canUseValue(readValue)) {
            val epicsValue = readValue.getOrElse(config.nullValue.value)

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
        } else {
            // todo: This should cause an error notification
            Nil
        }
    }

    private def canUseValue(readValue: Option[AnyRef]): Boolean = {
        readValue.isDefined || !config.isMandatory
    }

}
