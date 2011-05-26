package edu.gemini.aspen.gds.performancemonitoring

import org.scala_tools.time.Imports._
import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import actors.Actor
import scala.{Some, Option}

//Messages that EventLogger is supposed to handle must inherit this class
sealed abstract class EventLoggerMsg

//add a new event set to the EventLogger
case class AddEventSet(set: AnyRef) extends EventLoggerMsg

//store starting time of a given event
case class Start(set: AnyRef, evt: AnyRef) extends EventLoggerMsg

//store ending time of a given event
case class End(set: AnyRef, evt: AnyRef) extends EventLoggerMsg

//check if a given event was performed under a certain time. Returns Boolean
case class Check(set: AnyRef, evt: AnyRef, millis: Long) extends EventLoggerMsg

//retrieve the average time it took to execute certain event over all sets. Returns Duration
case class RetrieveEventAverage(evt: AnyRef) extends EventLoggerMsg

//Log the time it took to execute all events for a given event set
case class Log(set: AnyRef) extends EventLoggerMsg

//More specialized messages below:
//------------------------------------------------
case class Retrieve(set: AnyRef) extends EventLoggerMsg

case class RetrieveEvent(set: AnyRef, evt: AnyRef) extends EventLoggerMsg

case class RetrieveAll() extends EventLoggerMsg

case class LogAll() extends EventLoggerMsg

case class LogEventAverage(evt: AnyRef) extends EventLoggerMsg

//----------------------------------------------------

trait EventLogger extends Actor

/**
 * This event logger works by logging "event"'s starting and finishing times. The event are grouped in "sets".
 * For example, you can create a set for a datalabel, and log the starting and ending times of every observation event
 * received for said datalabel.
 *
 * You can then retrieve or log the data for a set, or average a certain event over all sets.
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[EventLogger]))
class EventLoggerImpl extends EventLogger {
  private val LOG = Logger.getLogger(this.getClass.getName)
  //map: eventSet -> (event -> (startTime, endTime))
  private val map = collection.mutable.Map.empty[AnyRef, collection.mutable.Map[AnyRef, (Option[DateTime], Option[DateTime])]]

  @Validate
  def validate() {
    start()
  }

  @Invalidate
  def invalidate() {
    this ! LogAll()
  }

  override def act() {
    loop {
      react {
        case AddEventSet(set) => _addEventSet(set)
        case Start(set, evt) => _start(set, evt)
        case End(set, evt) => _end(set, evt)
        case Log(set) => LOG.info("Timing stats for " + set + ": " + _retrieve(set))
        case LogAll() => LOG.info("Timing stats: " + _retrieveAll())
        case Retrieve(set) => reply(_retrieve(set))
        case RetrieveEvent(set, evt) => reply(_retrieve(set, evt))
        case Check(set, evt, millis) => reply(_retrieve(set, evt) match {
          case Some(x: Duration) => x.millis <= millis
          case _ => false
        })
        case RetrieveAll() => reply(_retrieveAll())
        case RetrieveEventAverage(evt) => reply(_average(evt))
        case LogEventAverage(evt) => LOG.info("Timing average for " + evt + ": " + _average(evt))
      }
    }
  }

  private def _addEventSet(set: AnyRef) {
    map += set -> collection.mutable.Map.empty[AnyRef, (Option[DateTime], Option[DateTime])]
  }

  private def _start(set: AnyRef, evt: AnyRef) {
    map.getOrElseUpdate(set, collection.mutable.Map.empty[AnyRef, (Option[DateTime], Option[DateTime])]) += evt -> (Some(DateTime.now), map(set).getOrElse(evt, (None, None))._2)
  }

  private def _end(set: AnyRef, evt: AnyRef) {
    map.getOrElseUpdate(set, collection.mutable.Map.empty[AnyRef, (Option[DateTime], Option[DateTime])]) += evt -> (map(set).getOrElse(evt, (None, None))._1, Some(DateTime.now))
  }

  private def _retrieve(set: AnyRef): scala.collection.Map[AnyRef, Option[Duration]] = {
    map.getOrElse(set, collection.mutable.Map.empty[AnyRef, (Option[DateTime], Option[DateTime])])
      .mapValues({
      case (Some(start), Some(end)) => Some((start to end).toDuration)
      case _ => None
    })
  }

  private def _retrieve(set: AnyRef, evt: AnyRef): Option[Duration] = {
    map.getOrElse(set, collection.mutable.Map.empty[AnyRef, (Option[DateTime], Option[DateTime])]).get(evt) flatMap {
      case (Some(start), Some(end)) => Some((start to end).toDuration)
      case _ => None
    }
  }

  private def _average(evt: AnyRef): Duration = {
    val values = for {
      (_, innerMap) <- map
      (_evt, times) <- innerMap
      if _evt == evt
    } yield times

    val durations = values.collect({
      case (Some(start), Some(end)) => (start to end).toDuration
    })

    case class Average(sum: Duration, count: Int) {
      def +(other: Duration): Average = {
        Average(sum + other, count + 1)
      }

      def average(): Duration = {
        count match {
          case 0 => 0.millis
          case x => (sum.millis / x).toInt.millis
        }
      }
    }
    val avg = new Average(Duration.standardSeconds(0), 0)
    durations.foldLeft(avg) {
      (currentAvg, currentVal) => currentAvg + currentVal
    }.average()
  }

  private def _retrieveAll() = {
    map.mapValues({
      case m => m.mapValues({
        case (Some(start), Some(end)) => Some((start to end).toDuration)
        case _ => None
      })
    })
  }
}
