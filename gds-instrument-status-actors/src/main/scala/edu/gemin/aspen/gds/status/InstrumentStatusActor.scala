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
                configuration.index.index)

        val statusItem = Option(statusDB.getStatusItem(channelName))
        def buildCollectedValues(value:AnyRef) = {
            CollectedValue(fitsKeyword, value, fitsComment, headerIndex) :: Nil
        }
        statusItem match {
            case Some(x) => {
                buildCollectedValues(x.getValue)
            }
            case None => {
                // In case no status item, we use the default value
                val value = configuration.nullValue.value
                buildCollectedValues(value)
            }
        }
    }

}
