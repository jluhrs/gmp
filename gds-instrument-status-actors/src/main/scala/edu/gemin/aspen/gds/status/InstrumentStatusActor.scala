package edu.gemin.aspen.gds.status

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.aspen.giapi.status.{StatusItem, StatusDatabaseService}

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
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

        val value = statusItem.getValue
        CollectedValue(fitsKeyword, value, fitsComment, headerIndex) :: Nil
    }

}
