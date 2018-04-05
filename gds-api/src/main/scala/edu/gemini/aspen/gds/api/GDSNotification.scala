package edu.gemini.aspen.gds.api

import java.time.Duration

import edu.gemini.aspen.giapi.data.{DataLabel, ObservationEvent}

/**
 * Parent trait of case classes defining notifications or events produced by the GDS */
sealed trait GDSNotification {
  val dataLabel: DataLabel
}

object GDSNotification {
  val GDSEventsTopic: String = "edu/gemini/aspen/gds/gdsevent"
  val GDSNotificationKey: String = "gdsevent"
}

/**
 * Notification that an observation has started
 * @param dataLabel The data label of the observation
 */
case class GDSStartObservation(dataLabel: DataLabel) extends GDSNotification

/**
 * Notification that an observation has completed
 * @param dataLabel The data label of the observation
 */
case class GDSEndObservation(dataLabel: DataLabel, writeTime: Long, keywords: Traversable[CollectedValue[_]] = Traversable.empty) extends GDSNotification

/**
 * Notification that an observation has stopped with an error
 * @param dataLabel The data label of the observation
 */
case class GDSObservationError(dataLabel: DataLabel, msg: String) extends GDSNotification

/**
 * Register how long observation stage have taken
 * @param dataLabel The data label of the observation
 * @param times List of the duration of each observation stage
 */
case class GDSObservationTimes(dataLabel: DataLabel, times: Traversable[(ObservationEvent, Option[Duration])]) extends GDSNotification
