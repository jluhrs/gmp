package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import org.scala_tools.time.Imports._
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusDatabaseService}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}

class PropertyValuesHelper(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) {
  def getStatus = {
    statusDB.getStatusItem("gpi:gds:health") match {
      case x: StatusItem[_] => if (x.getValue.equals(Health.GOOD)) {
        "<span style=\"color: green\">" + x.getValue.toString + "</span>"
      } else
      if (x.getValue.equals(Health.WARNING)) {
        "<span style=\"color: orange\">" + x.getValue.toString + "</span>"
      } else
      if (x.getValue.equals(Health.BAD)) {
        "<span style=\"color: red\">" + x.getValue.toString + "</span>"
      } else {
        x.getValue.toString
      }
      case _ => StatusModule.defaultStatus
    }
  }

  def isInError(label: DataLabel) = {
    obsState.isInError(label)
  }

  def getLastDataLabel = {
    getLastDataLabels(1).headOption.getOrElse(StatusModule.defaultLastDataLabel)
  }

  def getLastDataLabels(n: Int) = {
    obsState.getLastDataLabel(n) map {
      _.getName
    }
  }

  def getTimes: String = {
    obsState.getLastDataLabel map {
      getTimes(_)
    } getOrElse StatusModule.defaultTimes
  }

  def getTimes(label: DataLabel): String = {
    obsState.getTimes(label) filter {
      case (x: AnyRef, y: Option[_]) => x == "FITS update"
    } map {
      case (x: AnyRef, y: Option[_]) => y map {
        case t: Duration => t.getMillis.toString + "[ms]"
      } getOrElse ""
    } head
  }

  def getProcessing = {
    obsState.getObservationsInProgress.addString(new StringBuilder(), ", ").toString()
  }

  def getMissingKeywords: String = {
    obsState.getLastDataLabel map {
      getMissingKeywords(_)
    } getOrElse StatusModule.defaultMissing
  }

  def getMissingKeywords(label: DataLabel): String = {
    obsState.getMissingKeywords(label) map {
      case x: FitsKeyword => x.getName
    } addString(new StringBuilder(), ", ") toString()

  }

  def getKeywordsInError: String = {
    obsState.getLastDataLabel map {
      getKeywordsInError(_)
    } getOrElse StatusModule.defaultErrors
  }

  def getKeywordsInError(label: DataLabel): String = {
    obsState.getKeywordsInError(label) map {
      case (x: FitsKeyword, y: CollectionError.CollectionError) => x.getName
    } addString(new StringBuilder(), ",") toString()
  }

}