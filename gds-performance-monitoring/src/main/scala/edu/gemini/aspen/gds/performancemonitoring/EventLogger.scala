package edu.gemini.aspen.gds.performancemonitoring

import org.apache.felix.ipojo.annotations.{Validate, Provides, Instantiate, Component}
import actors.Reactor
import org.scala_tools.time.Imports._
import scala.{Some, Option}

sealed abstract class EventLoggerMsg

case class AddEventSet(set: Any) extends EventLoggerMsg

case class Start(set: Any, evt: Any) extends EventLoggerMsg

case class End(set: Any, evt: Any) extends EventLoggerMsg

case class Dump(set: Any) extends EventLoggerMsg

case class DumpAll() extends EventLoggerMsg

trait EventLogger extends Reactor[EventLoggerMsg] {
  def addEventSet(set: Any) {
    this ! AddEventSet(set)
  }
}

@Component
@Instantiate
@Provides(specifications = Array(classOf[EventLogger]))
class EventLoggerImpl extends EventLogger {

  @Validate
  def validate() {
    start()
  }

  override def act() {
    loop {
      react {
        case AddEventSet(set) => map += set -> collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])]
        case Start(set, evt) => map.getOrElseUpdate(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])]) += evt -> (Some(DateTime.now), map(set).getOrElse(evt, (None, None))._2)
        case End(set, evt) => map.getOrElseUpdate(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])]) += evt -> (map(set).getOrElse(evt, (None, None))._1, Some(DateTime.now))
        case Dump(set) => println(
          map.getOrElse(set, collection.mutable.Map.empty[Any, (Option[DateTime], Option[DateTime])])
            .mapValues({
            case (Some(start), Some(end)) => Some((start to end).toDuration)
            case _ => None
          }))
        case DumpAll() => println(map.mapValues({
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
