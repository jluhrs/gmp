package edu.gemini.aspen.gds.odb

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.aspen.giapi.status.{StatusItem, StatusDatabaseService}
import edu.gemini.pot.spdb.IDBDatabaseService

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class ODBValueActor(dbService: IDBDatabaseService, configuration:GDSConfiguration) extends KeywordValueActor {
    override def collectValues():List[CollectedValue] = {
        val (channelName, fitsKeyword, fitsComment, headerIndex) = (
                configuration.channel.name,
                configuration.keyword,
                configuration.fitsComment.value,
                configuration.index.index)

//        val statusItem = Option(statusDB.getStatusItem(channelName))
//        def buildCollectedValues(value:AnyRef) = {
//            CollectedValue(fitsKeyword, value, fitsComment, headerIndex) :: Nil
//        }
//        statusItem match {
//            case Some(x) => {
//                // TODO How to handle non string items
//                val value = x.asInstanceOf[StatusItem[String]].getValue
//                buildCollectedValues(value)
//            }
//            case None => {
//                // In case no status item, we use the default value
//                val value = configuration.nullValue.value
//                buildCollectedValues(value)
//            }
//        }
        List()
    }

}
