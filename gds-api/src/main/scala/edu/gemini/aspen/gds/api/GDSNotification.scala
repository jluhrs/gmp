package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import org.joda.time.Duration


/**
 * Parent trait of case classes defining notifications or events produced by the GDS */
sealed trait GDSNotification {
  val dataLabel:DataLabel
}

case class GDSStartObservation(dataLabel:DataLabel) extends GDSNotification

case class GDSEndObservation(dataLabel:DataLabel) extends GDSNotification

case class GDSObservationError(dataLabel:DataLabel, msg:String) extends GDSNotification

case class GDSObservationTimes(dataLabel:DataLabel, times:Traversable[(ObservationEvent, Option[Duration])]) extends GDSNotification