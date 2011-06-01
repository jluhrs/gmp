package edu.gemin.aspen.gds.status

import edu.gemini.aspen.gds.api.KeywordValueActor
import edu.gemini.aspen.gds.api.{GDSConfiguration, CollectedValue}
import edu.gemini.aspen.giapi.status.StatusDatabaseService

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class InstrumentStatusActor(statusDB: StatusDatabaseService, configuration: GDSConfiguration) extends KeywordValueActor {
  override def collectValues(): List[CollectedValue[_]] = {
    val statusItemName = configuration.channel.name
    val (fitsKeyword, fitsComment, headerIndex) = (
      configuration.keyword,
      configuration.fitsComment.value,
      configuration.index.index)

    val statusItem = Option(statusDB.getStatusItem(statusItemName))

    val value = statusItem match {
      case Some(x) => x.getValue
      // In case no status item, we use the default value
      case None => configuration.nullValue.value
    }

    CollectedValue(fitsKeyword, value, fitsComment, headerIndex) :: Nil
  }

}
