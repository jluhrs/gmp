package edu.gemini.aspen.gds.keywords.database

import actors.Actor
import edu.gemini.aspen.giapi.data.DataLabel

/**
  * Interface for the programId database */
trait ProgramIdDatabase extends Actor {
  def store(dataLabel: DataLabel, programId: String): Unit
}

//case classes define the messages accepted by the DataBase
sealed trait ProgramIdDatabaseAction

/** Message to indicate a programid should be stored */
case class StoreProgramId(dataLabel: DataLabel, programId: String) extends ProgramIdDatabaseAction

/** Message to request a programid for a dataLabel */
case class RetrieveProgramId(dataLabel: DataLabel) extends ProgramIdDatabaseAction