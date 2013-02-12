package edu.gemini.aspen.gds.status

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.StatusDatabaseService

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class InstrumentStatusActor(statusDB: StatusDatabaseService, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val s = System.currentTimeMillis()
    val statusItem = Option(statusDB.getStatusItem(sourceChannel))
    LOG.fine("Retrieving STATUS keyword " + fitsKeyword + " " + statusItem + " took " + (System.currentTimeMillis() - s) + "[ms]")
    statusItem.map {
      s => Option(s.getValue)
    }.map {
      x => valueToCollectedValue(x.get)
    }.toList
  }

}
