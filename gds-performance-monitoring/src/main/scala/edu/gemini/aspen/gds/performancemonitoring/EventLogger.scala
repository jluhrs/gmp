package edu.gemini.aspen.gds.performancemonitoring

import actors.Actor
import java.lang.Long
import scala.Some
import org.apache.felix.ipojo.annotations.{Validate, Provides, Instantiate, Component}

sealed abstract class EventLoggerMsg

case class AddEventSet(set: Any) extends EventLoggerMsg

case class Start(set: Any, evt: Any) extends EventLoggerMsg

case class End(set: Any, evt: Any) extends EventLoggerMsg

case class Dump(set: Any) extends EventLoggerMsg

case class DumpAll() extends EventLoggerMsg

trait EventLogger extends Actor

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
        case AddEventSet(set) => map += set -> collection.mutable.Map.empty[Any, (Option[Long], Option[Long])]
        case Start(set, evt) => map.getOrElseUpdate(set, collection.mutable.Map.empty[Any, (Option[Long], Option[Long])]) += evt -> (Some(1), map(set).getOrElse(evt, (None, None))._2)
        case End(set, evt) => map.getOrElseUpdate(set, collection.mutable.Map.empty[Any, (Option[Long], Option[Long])]) += evt -> (map(set).getOrElse(evt, (None, None))._1, Some(1))
        case Dump(set) => println(map.get(set))
        case DumpAll() => println(map)
      }
    }
  }

  //map: eventset -> (event -> (startTime, endTime))
  val map = collection.mutable.Map.empty[Any, collection.mutable.Map[Any, (Option[Long], Option[Long])]]


}
