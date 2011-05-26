package edu.gemini.aspen.gds.performancemonitoring

import org.scala_tools.time.Imports._
import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import actors.Actor
import scala.{Some, Option}

sealed abstract class EventLoggerMsg

case class AddEventSet(set: AnyRef) extends EventLoggerMsg

case class Start(set: AnyRef, evt: AnyRef) extends EventLoggerMsg

case class End(set: AnyRef, evt: AnyRef) extends EventLoggerMsg

case class Dump(set: AnyRef) extends EventLoggerMsg

case class DumpAll() extends EventLoggerMsg

case class Retrieve(set: AnyRef) extends EventLoggerMsg

case class RetrieveAll() extends EventLoggerMsg

case class RetrieveEventAverage(evt: AnyRef) extends EventLoggerMsg

case class DumpEventAverage(evt: AnyRef) extends EventLoggerMsg

trait EventLogger extends Actor

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
    this ! DumpAll()
  }

  override def act() {
    loop {
      react {
        case AddEventSet(set) => _addEventSet(set)
        case Start(set, evt) => _start(set, evt)
        case End(set, evt) => _end(set, evt)
        case Dump(set) => LOG.info("Timing stats for " + set + ": " + _retrieve(set))
        case DumpAll() => LOG.info("Timing stats: " + _retrieveAll())
        case Retrieve(set) => reply(_retrieve(set))
        case RetrieveAll() => reply(_retrieveAll())
        case RetrieveEventAverage(evt) => reply(_average(evt))
        case DumpEventAverage(evt) => LOG.info("Timing average for " + evt + ": " + _average(evt))
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

  private def _retrieve(set: AnyRef) = {
    map.getOrElse(set, collection.mutable.Map.empty[AnyRef, (Option[DateTime], Option[DateTime])])
      .mapValues({
      case (Some(start), Some(end)) => Some((start to end).toDuration)
      case _ => None
    })
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
