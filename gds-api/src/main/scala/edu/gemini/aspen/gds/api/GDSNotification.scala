package edu.gemini.aspen.gds.api

import edu.gemini.aspen.giapi.data.DataLabel

/**
 * Parent trait of case classes defining notifications or events produced by the GDS */
sealed trait GDSNotification {
  val dataLabel:DataLabel
}

case class GDSStartObservation(dataLabel:DataLabel) extends GDSNotification