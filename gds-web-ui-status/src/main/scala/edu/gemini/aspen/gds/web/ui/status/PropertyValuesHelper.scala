package edu.gemini.aspen.gds.web.ui.status

import edu.gemini.aspen.gds.observationstate.ObservationStateProvider
import edu.gemini.aspen.giapi.status.{Health, StatusItem, StatusDatabaseService}
import org.scala_tools.time.Imports._
import edu.gemini.aspen.gds.api.CollectionError
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.fits.FitsKeyword
import edu.gemini.aspen.gmp.top.Top
import org.apache.felix.ipojo.annotations.{Provides, Instantiate, Component}

sealed trait ObservationStatus
case object Successful extends ObservationStatus
case object MissingKeywords extends ObservationStatus
case object ErrorKeywords extends ObservationStatus
case object Timeout extends ObservationStatus
case object ObservationError extends ObservationStatus

@Component
@Instantiate
//@Provides(specifications = Array[Class[_]](classOf[ObservationsSource]))
class PropertyValuesHelper(statusDB: StatusDatabaseService, obsState: ObservationStateProvider, top: Top) {
  def getStatus = {
    statusDB.getStatusItem(top.buildStatusItemName("gds:health")) match {
      case x: StatusItem[_] => x.getValue.toString
      case _ => StatusModule.defaultStatus
    }
  }

  def getStatusStyle:String = {
    statusDB.getStatusItem(top.buildStatusItemName("gds:health")) match {
      case x: StatusItem[_] if (x.getValue.equals(Health.GOOD)) => "gds-green"
      case x: StatusItem[_] if (x.getValue.equals(Health.WARNING)) => "gds-orange"
      case x: StatusItem[_] if (x.getValue.equals(Health.BAD)) => "gds-red"
      case _ => ""
    }
  }

  def isInError(label: DataLabel) = obsState.isInError(label)

  def isFailed(label: DataLabel) = obsState.isFailed(label)

  def getLastDataLabel = {
    getLastDataLabels(1).headOption.getOrElse(StatusModule.defaultLastDataLabel)
  }

  def getLastDataLabels(n: Int):Traversable[String] = {
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
    val opt:Option[String] = obsState.getTimes(label) map {
      case (x: AnyRef, y: Option[_]) => y map {
        case t: Duration => t.getMillis.toString + "[ms]"
      } getOrElse("")
    } headOption

    opt.getOrElse(StatusModule.defaultTimes)
  }

  def getProcessing: String = obsState.getObservationsInProgress.mkString(", ")

  def getMissingKeywords: String = {
    obsState.getLastDataLabel map {
      getMissingKeywords(_)
    } getOrElse StatusModule.defaultMissing
  }

  def getMissingKeywords(label: DataLabel): String = {
    obsState.getMissingKeywords(label) map {
      case x: FitsKeyword => x.key
    } mkString(", ")
  }

  def getKeywordsInError: String = {
    obsState.getLastDataLabel map {
      getKeywordsInError(_)
    } getOrElse StatusModule.defaultErrors
  }

  def getKeywordsInError(label: DataLabel): String = {
    obsState.getKeywordsInError(label) map {
      case (x: FitsKeyword, y: CollectionError.CollectionError) => x.key
    } mkString(", ")
  }

  def getTimestamp(label: DataLabel): Option[DateTime] = {
    obsState.getTimestamp(label)
  }

  def getStatus(label: DataLabel): Option[ObservationStatus] = {
    if (isFailed(label) == Some(true)) {
      Some(ObservationError)
    } else if (getKeywordsInError(label).nonEmpty) {
      Some(ErrorKeywords)
    } else if (getMissingKeywords(label).nonEmpty) {
      Some(MissingKeywords)
    } else {
      Some(Successful)
    }
  }

}