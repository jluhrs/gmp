package edu.gemini.aspen.gds.observationstate

import java.time.LocalDateTime

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
case class ObservationInfo(@BeanProperty dataLabel: DataLabel, @BeanProperty result: ObservationStatus, @BeanProperty writeTime:Option[Long] = None, @BeanProperty timeStamp: LocalDateTime =  LocalDateTime.now(), @BeanProperty collectedValues: Traversable[CollectedValue[_]] = Traversable.empty, @BeanProperty errorMsg: Option[String] = None)