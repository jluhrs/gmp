package edu.gemini.aspen.gds.api

/**
  * Marker interface used to export GDSObseventHandlerImpl and used by the Health component */
trait GDSObseventHandler

object GDSObseventHandler {
  val ObsEventTopic = "edu/gemini/aspen/gds/obsevent/handler"
  val ObsEventKey = "observationevent"
}

