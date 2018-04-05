package edu.gemini.aspen.gds.observationstate

import org.joda.time.DateTime
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectedValue

import scala.beans.BeanProperty

sealed trait ObservationStatus
case object Successful extends ObservationStatus
case object MissingKeywords extends ObservationStatus
case object ErrorKeywords extends ObservationStatus
case object Timeout extends ObservationStatus
case object ObservationError extends ObservationStatus

/**
 * This class is used to report the final state of an observation
 */
case class ObservationInfo(@BeanProperty val dataLabel: DataLabel, @BeanProperty val result: ObservationStatus, @BeanProperty writeTime:Option[Long] = None, @BeanProperty val timeStamp: DateTime =  new DateTime(), @BeanProperty collectedValues: Traversable[CollectedValue[_]] = Traversable.empty, @BeanProperty errorMsg: Option[String] = None)