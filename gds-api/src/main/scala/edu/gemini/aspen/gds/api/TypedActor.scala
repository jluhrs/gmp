package edu.gemini.aspen.gds.api

trait TypedActor[M] {
  val channel: scala.actors.Channel[M]

  def send(msg: M): Any = {
    channel !? msg
  }
}