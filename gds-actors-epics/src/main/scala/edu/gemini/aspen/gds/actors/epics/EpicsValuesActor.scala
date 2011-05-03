package edu.gemini.aspen.gds.actors.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.actors.KeywordValueActor
import edu.gemini.aspen.gds.api.CollectedValue
import edu.gemini.aspen.gds.keywordssets.configuration.{GDSConfiguration, HeaderIndex}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration:GDSConfiguration) extends KeywordValueActor {
    override def collectValues():List[CollectedValue] = {

        val (channelName, fitsKeyword, fitsComment, headerIndex) = (
                configuration.channel.name,
                configuration.keyword,
                configuration.fitsComment.value,
                configuration.index.index
                )

        val epicsValue = epicsReader.getValue(channelName)
        if (epicsValue.isInstanceOf[Array[Double]]) {
            // TODO: This should be done on the EpicsArray Actor
            CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](0).asInstanceOf[AnyRef], fitsComment, headerIndex) :: Nil
        } else {
            // TODO cast to the right type
            CollectedValue(fitsKeyword, epicsValue, fitsComment, headerIndex) :: Nil
        }
    }

}
