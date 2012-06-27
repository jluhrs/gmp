package edu.gemini.aspen.gds.observationstate

import reflect.BeanProperty
import org.joda.time.DateTime
import edu.gemini.aspen.giapi.data.DataLabel

sealed trait ObservationStatus
case object Successful extends ObservationStatus
case object MissingKeywords extends ObservationStatus
case object ErrorKeywords extends ObservationStatus
case object Timeout extends ObservationStatus
case object ObservationError extends ObservationStatus

/**
 * This class is used to report the final state of an observation
 */
case class ObservationInfo(@BeanProperty val dataLabel: DataLabel, @BeanProperty val result: ObservationStatus, @BeanProperty val timeStamp: DateTime =  new DateTime(), @BeanProperty errorMsg: String = "")