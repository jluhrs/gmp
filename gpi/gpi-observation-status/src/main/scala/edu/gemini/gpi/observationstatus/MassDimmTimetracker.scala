package edu.gemini.gpi.observationstatus

import java.util

import edu.gemini.aspen.giapi.status.impl.BasicStatus
import edu.gemini.aspen.giapi.status.setter.StatusSetter
import edu.gemini.epics.api.EpicsClient
import edu.gemini.gmp.top.Top

/**
  * Records updates to epics channels and records the timestamp in a status item
  */
class MassDimmTimetracker(top: Top, statusSetter: StatusSetter) extends EpicsClient {
  val massLastUpdate = top.buildStatusItemName("massLastUpdate")
  val dimmLastUpdate = top.buildStatusItemName("dimmLastUpdate")

  var lastMassUpdate: Option[Long] = None
  var lastDimmUpdate: Option[Long] = None

  override def valueChanged[T](channel: String, values: util.List[T]): Unit = {
    lastMassUpdate = updateTimestamp(channel, MassDimmTimetracker.massEpicsChannel, massLastUpdate, lastMassUpdate)
    lastDimmUpdate = updateTimestamp(channel, MassDimmTimetracker.dimmEpicsChannel, dimmLastUpdate, lastDimmUpdate)
  }

  private def updateTimestamp[T](channel: String, epicsChannelName: String, destStatusItem: String, lastUpdate: Option[Long]): Option[Long] = {
    if (channel == epicsChannelName) {
      val timestamp = System.currentTimeMillis() / 1000
      lastUpdate.foreach { _ =>
        // Export as seconds instead of milliseconds since we can only produce Int as status items
        statusSetter.setStatusItem(new BasicStatus[Int](destStatusItem, timestamp.toInt))
      }
      Some(timestamp)
    } else {
      lastUpdate
    }
  }

  override def connected(): Unit = {}

  override def disconnected(): Unit = {}
}

object MassDimmTimetracker {
  val massEpicsChannel = "ws:massFsee"
  val dimmEpicsChannel = "ws:seeFwhm"
}
