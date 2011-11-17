package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusDatabaseService}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}

class PropertyValuesHelper(statusDB: StatusDatabaseService, obsState: ObservationStateProvider) {
  def getStatus = {
    statusDB.getStatusItem("gpi:gds:health") match {
      case x: StatusItem[_] => x.getValue.toString
      case _ => StatusModule.defaultStatus
    }
  }

  def getStatusStyle:String = {
    statusDB.getStatusItem("gpi:gds:health") match {
      case x: StatusItem[_] if (x.getValue.equals(Health.GOOD)) => "gds-green"
      case x: StatusItem[_] if (x.getValue.equals(Health.WARNING)) => "gds-orange"
      case x: StatusItem[_] if (x.getValue.equals(Health.BAD)) => "gds-red"
      case _ => ""
    }
  }

  def isInError(label: DataLabel) = obsState.isInError(label)

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
    obsState.getTimes(label) map {
      case (x: AnyRef, y: Option[_]) => y map {
        case t: Duration => t.getMillis.toString + "[ms]"
      } getOrElse("")
    } head
  }

  def getProcessing: String = obsState.getObservationsInProgress.mkString(", ")

  def getMissingKeywords: String = {
    obsState.getLastDataLabel map {
      getMissingKeywords(_)
    } getOrElse StatusModule.defaultMissing
  }

  def getMissingKeywords(label: DataLabel): String = {
    obsState.getMissingKeywords(label) map {
      case x: FitsKeyword => x.getName
    } mkString(", ")
  }

  def getKeywordsInError: String = {
    obsState.getLastDataLabel map {
      getKeywordsInError(_)
    } getOrElse StatusModule.defaultErrors
  }

  def getKeywordsInError(label: DataLabel): String = {
    obsState.getKeywordsInError(label) map {
      case (x: FitsKeyword, y: CollectionError.CollectionError) => x.getName
    } mkString(", ")
  }

}