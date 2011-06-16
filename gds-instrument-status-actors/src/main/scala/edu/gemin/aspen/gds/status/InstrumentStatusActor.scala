package edu.gemin.aspen.gds.status

import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.giapi.status.{StatusItem, StatusDatabaseService}

/**
 * Actor that can produce as a reply of a Collect request with the value of an StatusItem linked
 * to a single fitsKeyword
 */
class InstrumentStatusActor(statusDB: StatusDatabaseService, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
  override def collectValues(): List[CollectedValue[_]] = {
    val statusItem = Option(statusDB.getStatusItem(sourceChannel))

    statusItem map (collectedValue) orElse (defaultCollectedValue) toList
  }

  def collectedValue(status: StatusItem[_]): CollectedValue[_] = {
    dataType match {
      case DataType("STRING") => CollectedValue(fitsKeyword, status.getValue.toString, fitsComment, headerIndex)
      case DataType("DOUBLE") => CollectedValue(fitsKeyword, status.getValue.asInstanceOf[Double], fitsComment, headerIndex)
      case DataType("INT") => CollectedValue(fitsKeyword, status.getValue.asInstanceOf[Int], fitsComment, headerIndex)
    }
  }

}
