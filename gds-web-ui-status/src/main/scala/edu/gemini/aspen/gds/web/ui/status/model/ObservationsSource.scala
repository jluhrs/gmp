package edu.gemini.aspen.gds.web.ui.status.model

import reflect.BeanProperty
import org.joda.time.{DateTimeZone, DateTime}

import edu.gemini.aspen.gds.web.ui.status.ObservationStatus
import org.joda.time.format.ISODateTimeFormat

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
case class ObservationBean(@BeanProperty val result:ObservationStatus, timeStamp0: Option[DateTime], @BeanProperty val dataLabel: String) {

  /**Expose properties as JavaBean properties */
  @BeanProperty val timeStamp = ObservationBean.formatTimeStamp(timeStamp0.getOrElse(new DateTime()))
}

object ObservationBean {
  val timeStampFormatter = ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC)

  /**Formats the timestamp output */
  def formatTimeStamp(timeStamp: DateTime) = timeStampFormatter.print(timeStamp)

}