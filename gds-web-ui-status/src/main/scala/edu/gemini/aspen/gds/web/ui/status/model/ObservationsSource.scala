package edu.gemini.aspen.gds.web.ui.status.model

import reflect.BeanProperty
import org.joda.time.DateTime

import edu.gemini.aspen.gds.web.ui.status.ObservationStatus

/**
 * This class is used by the LazyQueryContainer to read beans representing log values to display on the screen
 * The BeanQuery in this case is read only */
case class ObservationBean(@BeanProperty val result:ObservationStatus, timeStamp0: Option[DateTime], @BeanProperty val dataLabel: String) {

  /**Expose properties as JavaBean properties */
  //@BeanProperty val timeStamp = LoggingEventBeanQuery.formatTimeStamp(timeStamp0)
  @BeanProperty val timeStamp = timeStamp0.getOrElse(new DateTime())
}

/**
 * Interface for the OSGiService */
trait ObservationsSource {
  def observations: Iterable[ObservationBean]
}