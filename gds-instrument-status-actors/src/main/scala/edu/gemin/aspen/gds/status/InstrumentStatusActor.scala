package edu.gemin.aspen.gds.status

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class InstrumentStatusActor(configuration:GDSConfiguration) extends KeywordValueActor {
    override def collectValues():List[CollectedValue] = {

        val (channelName, fitsKeyword, fitsComment, headerIndex) = (
                configuration.channel.name,
                configuration.keyword,
                configuration.fitsComment.value,
                configuration.index.index
                )

//        val epicsValue = epicsReader.getValue(channelName)
//
//        if (epicsValue.isInstanceOf[Array[Double]]) {
//            // TODO: This should be done on the EpicsArray Actor
//            CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](0).asInstanceOf[AnyRef], fitsComment, headerIndex) :: Nil
//        } else {
//            // TODO cast to the right type
//            CollectedValue(fitsKeyword, epicsValue, fitsComment, headerIndex) :: Nil
//        }
        List()
    }

}
