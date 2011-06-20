package edu.gemin.aspen.gds.status

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.{StatusItem, StatusDatabaseService}
import java.util.logging.Logger

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class InstrumentStatusActor(statusDB: StatusDatabaseService, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    private val LOG = Logger.getLogger(this.getClass.getName)

    override def collectValues(): List[CollectedValue[_]] = {
        val statusItem = Option(statusDB.getStatusItem(sourceChannel))
        List(statusItem map { s => Option(s.getValue) } map { x => valueToCollectedValue(x.get) } getOrElse (defaultCollectedValue))
    }

}
