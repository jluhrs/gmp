package edu.gemini.aspen.gds.performancemonitoring

import org.scala_tools.time.Imports._
import scala.{Some, Option}
import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import actors.Actor

sealed abstract class EventLoggerMsg

case class AddEventSet(set: Any) extends EventLoggerMsg

case class Start(set: Any, evt: Any) extends EventLoggerMsg

case class End(set: Any, evt: Any) extends EventLoggerMsg

case class Dump(set: Any) extends EventLoggerMsg

case class DumpAll() extends EventLoggerMsg

case class Retrieve(set: Any) extends EventLoggerMsg

case class RetrieveAll() extends EventLoggerMsg

trait EventLogger extends Actor {
  def addEventSet(set: Any) {
    this ! AddEventSet(set)
  }
}

@Component
@Instantiate
@Provides(specifications = Array(classOf[EventLogger]))
class EventLoggerImpl extends EventLogger {
  private val LOG = Logger.getLogger(this.getClass.getName)

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
        case AddEventSet(set) => map += set -> collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])]
        case Start(set, evt) => map.getOrElseUpdate(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])]) += evt -> (Some(DateTime.now), map(set).getOrElse(evt, (None, None))._2)
        case End(set, evt) => map.getOrElseUpdate(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])]) += evt -> (map(set).getOrElse(evt, (None, None))._1, Some(DateTime.now))
        case Dump(set) => LOG.info("Timing stats for " + set + ": " +
          map.getOrElse(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])])
            .mapValues({
            case (Some(start), Some(end)) => Some((start to end).toDuration)
            case _ => None
          }))
        case DumpAll() => LOG.info("Timing stats: " + map.mapValues({
          case m => m.mapValues({
            case (Some(start), Some(end)) => Some((start to end).toDuration)
            case _ => None
          })
        }))
        case Retrieve(set) => reply(map.getOrElse(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])])
          .mapValues({
          case (Some(start), Some(end)) => Some((start to end).toDuration)
          case _ => None
        }))
        case RetrieveAll() => reply(map.mapValues({
          case m => m.mapValues({
            case (Some(start), Some(end)) => Some((start to end).toDuration)
            case _ => None
          })
        }))
      }
    }
  }

  //map: eventset -> (event -> (startTime, endTime))
  val map = collection.mutable.Map.empty[Any, collection.mutable.Map[Any, (Option[DateTime], Option[DateTime])]]


}
