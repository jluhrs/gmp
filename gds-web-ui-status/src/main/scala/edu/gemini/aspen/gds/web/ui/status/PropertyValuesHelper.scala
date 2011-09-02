package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import com.vaadin.ui.Label
import org.scala_tools.time.Imports._
import edu.gemini.aspen.giapi.status.{Health, HealthStatusItem, StatusItem, StatusDatabaseService}

class PropertyValuesHelper(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) {
  def getStatus = {
    statusDB.getStatusItem("gpi:gds:health") match {
      case x: StatusItem[_] => if (x.getValue == Health.GOOD) {
        "<span style=\"color: green\">" + x.getValue.toString + "</span>"
      } else
      if (x.getValue == Health.WARNING) {
        "<span style=\"color: orange\">" + x.getValue.toString + "</span>"
      } else
      if (x.getValue == Health.BAD) {
        "<span style=\"color: red\">" + x.getValue.toString + "</span>"
      } else {
        x.getValue.toString
      }
      case _ => StatusModule.defaultStatus
    }
  }

  def getLastDataLabel = {
    obsState.getLastDataLabel map {
      _.getName
    } getOrElse StatusModule.defaultLastDataLabel
  }

  def getTimes = {
    obsState.getLastDataLabel map {
      obsState.getTimes(_) map {
        case (x: AnyRef, y: Option[Duration]) => (x, y.getOrElse(""))
      } toString
    } getOrElse StatusModule.defaultTimes
  }

  def getProcessing = {
    obsState.getObservationsInProgress.toString()
  }

  def getMissingKeywords = {
    obsState.getLastDataLabel map {
      obsState.getMissingKeywords(_).toString()
    } getOrElse StatusModule.defaultMissing
  }

  def getKeywordsInError = {
    obsState.getLastDataLabel map {
      obsState.getKeywordsInError(_).toString()
    } getOrElse StatusModule.defaultErrors
  }
}