package edu.gemin.aspen.gds.status

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.aspen.giapi.status.{StatusItem, StatusDatabaseService}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class InstrumentStatusActor(statusDB: StatusDatabaseService, configuration:GDSConfiguration) extends KeywordValueActor {
    override def collectValues():List[CollectedValue] = {

        val (channelName, fitsKeyword, fitsComment, headerIndex) = (
                configuration.channel.name,
                configuration.keyword,
                configuration.fitsComment.value,
                configuration.index.index
                )

        val statusItem:StatusItem[AnyRef] = statusDB.getStatusItem(channelName)


//
//        if (epicsValue.isInstanceOf[Array[Double]]) {
//            // TODO: This should be done on the EpicsArray Actor
//            CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](0).asInstanceOf[AnyRef], fitsComment, headerIndex) :: Nil
//        } else {
        statusItem match {
            // TODO cast to the right type
            case v:StatusItem[AnyRef] => CollectedValue(fitsKeyword, v.getValue, fitsComment, headerIndex) :: Nil
        }
//        }
    }

}
