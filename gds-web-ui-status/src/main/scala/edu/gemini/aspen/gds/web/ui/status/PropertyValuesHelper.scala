package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import org.scala_tools.time.Imports._
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusDatabaseService}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.giapi.data.FitsKeyword
import edu.gemini.aspen.gds.api.CollectionError

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
      obsState.getTimes(_) filter {
        case (x: AnyRef, y: Option[Duration]) => x == "FITS update"
      } map {
        case (x: AnyRef, y: Option[Duration]) => y map {
          case t: Duration => t.getMillis.toString + "[ms]"
        } getOrElse ""
      } head
    } getOrElse StatusModule.defaultTimes
  }

  def getProcessing = {
    obsState.getObservationsInProgress.addString(new StringBuilder(), ",").toString()
  }

  def getMissingKeywords = {
    obsState.getLastDataLabel map {
      obsState.getMissingKeywords(_) map {
        case x: FitsKeyword => x.getName
      } addString(new StringBuilder(), ", ") toString()
    } getOrElse StatusModule.defaultMissing
  }

  def getKeywordsInError = {
    obsState.getLastDataLabel map {
      obsState.getKeywordsInError(_) map {
        case (x: FitsKeyword, y: CollectionError.CollectionError) => x.getName
      } addString(new StringBuilder(), ",") toString()
    } getOrElse StatusModule.defaultErrors
  }
}